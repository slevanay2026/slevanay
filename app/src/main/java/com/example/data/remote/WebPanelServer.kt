package com.example.data.remote

import android.util.Log
import com.example.data.local.XtreamProfileEntity
import com.example.data.repository.FreeKurdishChannel
import com.example.ui.i18n.AppLanguage
import com.example.ui.theme.AppThemeOption
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedInputStream
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.net.Inet4Address
import java.net.InetAddress
import java.net.NetworkInterface
import java.net.ServerSocket
import java.net.Socket
import java.nio.charset.StandardCharsets

data class WebPanelSnapshot(
    val activeProfile: XtreamProfileEntity?,
    val profiles: List<XtreamProfileEntity>,
    val liveCount: Int,
    val vodCount: Int,
    val seriesCount: Int,
    val appTheme: AppThemeOption,
    val appLanguage: AppLanguage,
    val remoteWebsiteUrl: String,
    val remoteAnnouncement: String?,
    val customChannels: List<FreeKurdishChannel>
)

interface WebPanelCommandHandler {
    fun getSnapshot(): WebPanelSnapshot
    fun connectXtreamFromWeb(name: String, serverUrl: String, username: String, password: String)
    fun switchProfileFromWeb(profileId: Long)
    fun syncActiveFromWeb()
    fun playStreamUrlFromWeb(title: String, streamUrl: String)
    fun addCustomChannelFromWeb(name: String, streamUrl: String, category: String, dialect: String)
    fun removeCustomChannelFromWeb(streamUrl: String)
    fun setThemeFromWeb(themeId: String)
    fun setLanguageFromWeb(languageCode: String)
    fun syncRemoteWebsiteFromWeb(websiteUrl: String)
}

class WebPanelServer(
    private val scope: CoroutineScope,
    private val handler: WebPanelCommandHandler
) {
    private var serverSocket: ServerSocket? = null
    private var acceptJob: Job? = null

    @Volatile
    var isRunning: Boolean = false
        private set

    @Volatile
    var activePort: Int = DEFAULT_PORT
        private set

    fun start(preferredPort: Int = DEFAULT_PORT): String? {
        if (isRunning && serverSocket != null) {
            return buildPanelUrl(activePort)
        }
        stop()

        val portsToTry = (preferredPort..(preferredPort + 6)).toList()
        var boundSocket: ServerSocket? = null
        for (port in portsToTry) {
            try {
                boundSocket = ServerSocket(port, 32, InetAddress.getByName("0.0.0.0"))
                activePort = port
                break
            } catch (_: Exception) {
                // try next port
            }
        }
        val socket = boundSocket ?: return null
        serverSocket = socket
        isRunning = true

        acceptJob = scope.launch(Dispatchers.IO) {
            while (isActive && isRunning && !socket.isClosed) {
                try {
                    val client = socket.accept()
                    launch(Dispatchers.IO) {
                        handleClient(client)
                    }
                } catch (e: CancellationException) {
                    throw e
                } catch (_: Exception) {
                    if (socket.isClosed) break
                }
            }
        }
        return buildPanelUrl(activePort)
    }

    fun stop() {
        isRunning = false
        acceptJob?.cancel()
        acceptJob = null
        runCatching { serverSocket?.close() }
        serverSocket = null
    }

    private fun handleClient(socket: Socket) {
        socket.use { client ->
            client.soTimeout = 8000
            val input = BufferedInputStream(client.getInputStream())
            val output = client.getOutputStream()

            val requestLine = readHttpLine(input) ?: return
            val parts = requestLine.split(" ")
            if (parts.size < 2) return
            val method = parts[0].uppercase()
            val rawPath = parts[1].substringBefore('?')

            var contentLength = 0
            while (true) {
                val headerLine = readHttpLine(input) ?: break
                if (headerLine.isEmpty()) break
                val colonIdx = headerLine.indexOf(':')
                if (colonIdx > 0) {
                    val key = headerLine.substring(0, colonIdx).trim().lowercase()
                    val value = headerLine.substring(colonIdx + 1).trim()
                    if (key == "content-length") {
                        contentLength = value.toIntOrNull()?.coerceIn(0, 256 * 1024) ?: 0
                    }
                }
            }

            if (method == "OPTIONS") {
                writeResponse(output, 204, "text/plain", "")
                return
            }

            val bodyText = if (contentLength > 0) {
                val buf = ByteArray(contentLength)
                var totalRead = 0
                while (totalRead < contentLength) {
                    val r = input.read(buf, totalRead, contentLength - totalRead)
                    if (r <= 0) break
                    totalRead += r
                }
                String(buf, 0, totalRead, StandardCharsets.UTF_8)
            } else {
                ""
            }

            try {
                when {
                    method == "GET" && rawPath == "/" -> {
                        writeResponse(output, 200, "text/html; charset=utf-8", DASHBOARD_HTML)
                    }
                    method == "GET" && rawPath == "/api/state" -> {
                        val json = buildSnapshotJson(handler.getSnapshot())
                        writeResponse(output, 200, "application/json; charset=utf-8", json)
                    }
                    method == "POST" && rawPath == "/api/xtream/connect" -> {
                        val obj = JSONObject(bodyText)
                        val name = obj.optString("name").trim()
                        val serverUrl = obj.optString("serverUrl").trim()
                        val username = obj.optString("username").trim()
                        val password = obj.optString("password").trim()
                        if (serverUrl.isBlank() || username.isBlank() || password.isBlank()) {
                            writeJsonError(output, 400, "Sunucu URL, kullanıcı adı ve şifre zorunludur.")
                        } else {
                            handler.connectXtreamFromWeb(name, serverUrl, username, password)
                            writeJsonOk(output, "Xtream panel bağlantısı TV üzerinde başlatıldı.")
                        }
                    }
                    method == "POST" && rawPath == "/api/xtream/switch" -> {
                        val obj = JSONObject(bodyText)
                        val profileId = obj.optLong("profileId", -1L)
                        if (profileId <= 0L) {
                            writeJsonError(output, 400, "Geçersiz panel ID.")
                        } else {
                            handler.switchProfileFromWeb(profileId)
                            writeJsonOk(output, "Aktif panel değiştirildi.")
                        }
                    }
                    method == "POST" && rawPath == "/api/xtream/sync" -> {
                        handler.syncActiveFromWeb()
                        writeJsonOk(output, "Katalog senkronizasyonu başlatıldı.")
                    }
                    method == "POST" && rawPath == "/api/play" -> {
                        val obj = JSONObject(bodyText)
                        val streamUrl = obj.optString("streamUrl").trim()
                        val title = obj.optString("title").ifBlank { "Web Panel Yayını" }.trim()
                        if (streamUrl.isBlank()) {
                            writeJsonError(output, 400, "Yayın linki (URL) gerekli.")
                        } else {
                            handler.playStreamUrlFromWeb(title, streamUrl)
                            writeJsonOk(output, "Yayın TV ekranında açılıyor: $title")
                        }
                    }
                    method == "POST" && rawPath == "/api/custom-channel/add" -> {
                        val obj = JSONObject(bodyText)
                        val name = obj.optString("name").trim()
                        val streamUrl = obj.optString("streamUrl").trim()
                        val category = obj.optString("category").ifBlank { "Web Panel" }.trim()
                        val dialect = obj.optString("dialect").ifBlank { "general" }.trim()
                        if (name.isBlank() || streamUrl.isBlank()) {
                            writeJsonError(output, 400, "Kanal adı ve yayın URL'si gerekli.")
                        } else {
                            handler.addCustomChannelFromWeb(name, streamUrl, category, dialect)
                            writeJsonOk(output, "Kanal eklendi: $name")
                        }
                    }
                    method == "POST" && rawPath == "/api/custom-channel/delete" -> {
                        val obj = JSONObject(bodyText)
                        val streamUrl = obj.optString("streamUrl").trim()
                        handler.removeCustomChannelFromWeb(streamUrl)
                        writeJsonOk(output, "Kanal kaldırıldı.")
                    }
                    method == "POST" && rawPath == "/api/settings" -> {
                        val obj = JSONObject(bodyText)
                        val themeId = obj.optString("themeId")
                        val langCode = obj.optString("languageCode")
                        if (themeId.isNotBlank()) handler.setThemeFromWeb(themeId)
                        if (langCode.isNotBlank()) handler.setLanguageFromWeb(langCode)
                        writeJsonOk(output, "Ayarlar güncellendi.")
                    }
                    method == "POST" && rawPath == "/api/remote-website" -> {
                        val obj = JSONObject(bodyText)
                        val websiteUrl = obj.optString("websiteUrl").trim()
                        if (websiteUrl.isBlank()) {
                            writeJsonError(output, 400, "Web sitesi URL'si gerekli.")
                        } else {
                            handler.syncRemoteWebsiteFromWeb(websiteUrl)
                            writeJsonOk(output, "Web sitesi paneli senkronize ediliyor...")
                        }
                    }
                    else -> {
                        writeJsonError(output, 404, "Endpoint bulunamadı")
                    }
                }
            } catch (e: Exception) {
                Log.w(TAG, "WebPanelServer request failed", e)
                writeJsonError(output, 500, e.message ?: "Sunucu hatası")
            }
        }
    }

    private fun buildSnapshotJson(snapshot: WebPanelSnapshot): String {
        val root = JSONObject().apply {
            put("appName", "JHE HAKAR")
            put("liveCount", snapshot.liveCount)
            put("vodCount", snapshot.vodCount)
            put("seriesCount", snapshot.seriesCount)
            put("themeId", snapshot.appTheme.id)
            put("languageCode", snapshot.appLanguage.code)
            put("remoteWebsiteUrl", snapshot.remoteWebsiteUrl)
            put("remoteAnnouncement", snapshot.remoteAnnouncement ?: "")
            put(
                "activeProfile",
                snapshot.activeProfile?.let { p ->
                    JSONObject().apply {
                        put("id", p.id)
                        put("name", p.name)
                        put("serverUrl", p.serverUrl)
                        put("username", p.username)
                        put("status", p.status)
                    }
                } ?: JSONObject.NULL
            )
            put(
                "profiles",
                JSONArray().apply {
                    snapshot.profiles.forEach { p ->
                        put(
                            JSONObject().apply {
                                put("id", p.id)
                                put("name", p.name)
                                put("serverUrl", p.serverUrl)
                                put("username", p.username)
                                put("isActive", p.isActive)
                                put("status", p.status)
                            }
                        )
                    }
                }
            )
            put(
                "customChannels",
                JSONArray().apply {
                    snapshot.customChannels.forEach { ch ->
                        put(
                            JSONObject().apply {
                                put("name", ch.name)
                                put("streamUrl", ch.streamUrl)
                                put("category", ch.category)
                                put("dialect", ch.dialect.id)
                            }
                        )
                    }
                }
            )
        }
        return root.toString()
    }

    private fun readHttpLine(input: InputStream): String? {
        val out = ByteArrayOutputStream()
        var prev = -1
        while (true) {
            val b = input.read()
            if (b == -1) {
                return if (out.size() == 0) null else out.toString(StandardCharsets.UTF_8.name()).trimEnd('\r')
            }
            if (b == '\n'.code) {
                val str = out.toString(StandardCharsets.UTF_8.name())
                return if (prev == '\r'.code && str.endsWith('\r')) str.dropLast(1) else str
            }
            prev = b
            out.write(b)
            if (out.size() > 8192) break
        }
        return out.toString(StandardCharsets.UTF_8.name())
    }

    private fun writeJsonOk(output: OutputStream, message: String) {
        val body = JSONObject().apply {
            put("ok", true)
            put("message", message)
        }.toString()
        writeResponse(output, 200, "application/json; charset=utf-8", body)
    }

    private fun writeJsonError(output: OutputStream, status: Int, error: String) {
        val body = JSONObject().apply {
            put("ok", false)
            put("error", error)
        }.toString()
        writeResponse(output, status, "application/json; charset=utf-8", body)
    }

    private fun writeResponse(output: OutputStream, status: Int, contentType: String, body: String) {
        val statusText = when (status) {
            200 -> "OK"
            204 -> "No Content"
            400 -> "Bad Request"
            404 -> "Not Found"
            else -> "Internal Server Error"
        }
        val bodyBytes = body.toByteArray(StandardCharsets.UTF_8)
        val headers = buildString {
            append("HTTP/1.1 $status $statusText\r\n")
            append("Content-Type: $contentType\r\n")
            append("Content-Length: ${bodyBytes.size}\r\n")
            append("Access-Control-Allow-Origin: *\r\n")
            append("Access-Control-Allow-Methods: GET, POST, OPTIONS\r\n")
            append("Access-Control-Allow-Headers: Content-Type\r\n")
            append("Connection: close\r\n\r\n")
        }.toByteArray(StandardCharsets.US_ASCII)
        output.write(headers)
        if (bodyBytes.isNotEmpty()) {
            output.write(bodyBytes)
        }
        output.flush()
    }

    companion object {
        private const val TAG = "WebPanelServer"
        const val DEFAULT_PORT = 8088

        fun buildPanelUrl(port: Int): String {
            val ip = resolveLocalLanIpAddress() ?: "127.0.0.1"
            return "http://$ip:$port"
        }

        fun resolveLocalLanIpAddress(): String? {
            return runCatching {
                val interfaces = NetworkInterface.getNetworkInterfaces()?.toList().orEmpty()
                val candidates = mutableListOf<Pair<Int, String>>()
                for (ni in interfaces) {
                    if (!ni.isUp || ni.isLoopback || ni.isVirtual) continue
                    val name = ni.name.lowercase()
                    val priority = when {
                        name.startsWith("wlan") || name.startsWith("eth") || name.startsWith("en") -> 0
                        name.startsWith("ap") || name.startsWith("rmnet") -> 1
                        else -> 2
                    }
                    for (addr in ni.inetAddresses) {
                        if (addr is Inet4Address && !addr.isLoopbackAddress) {
                            val ip = addr.hostAddress ?: continue
                            if (addr.isSiteLocalAddress) {
                                candidates.add(priority to ip)
                            } else {
                                candidates.add((priority + 3) to ip)
                            }
                        }
                    }
                }
                candidates.minByOrNull { it.first }?.second
            }.getOrNull()
        }

        private val DASHBOARD_HTML = """
<!DOCTYPE html>
<html lang="tr">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width,initial-scale=1.0">
<title>JHE HAKAR — Web Yönetim Paneli</title>
<style>
  :root { --bg:#070B12; --card:#0D1526; --surface:#131C2E; --border:#283754; --cyan:#00E5FF; --green:#10B981; --red:#FF5252; --text:#F8FAFC; --muted:#94A3B8; }
  * { box-sizing: border-box; font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, sans-serif; }
  body { margin:0; background:var(--bg); color:var(--text); padding:20px; }
  .wrap { max-width:960px; margin:0 auto; display:grid; gap:18px; }
  .header { display:flex; flex-wrap:wrap; align-items:center; justify-content:space-between; gap:12px; background:var(--card); border:1px solid var(--border); border-radius:18px; padding:18px 22px; }
  .brand h1 { margin:0; font-size:22px; letter-spacing:0.5px; color:var(--cyan); }
  .brand p { margin:4px 0 0; color:var(--muted); font-size:13px; }
  .badge { background:rgba(16,185,129,.16); color:var(--green); border:1px solid rgba(16,185,129,.45); padding:6px 12px; border-radius:999px; font-size:12px; font-weight:700; }
  .stats { display:grid; grid-template-columns:repeat(auto-fit,minmax(160px,1fr)); gap:12px; }
  .stat { background:var(--card); border:1px solid var(--border); border-radius:16px; padding:16px; }
  .stat .label { color:var(--muted); font-size:12px; }
  .stat .val { font-size:24px; font-weight:800; margin-top:6px; color:var(--cyan); }
  .grid { display:grid; grid-template-columns:repeat(auto-fit,minmax(300px,1fr)); gap:16px; }
  .card { background:var(--card); border:1px solid var(--border); border-radius:18px; padding:18px; display:flex; flex-direction:column; gap:10px; }
  .card h2 { margin:0; font-size:16px; color:var(--text); }
  .card p.sub { margin:0; font-size:12px; color:var(--muted); }
  input, select { width:100%; background:var(--surface); color:var(--text); border:1px solid var(--border); border-radius:10px; padding:10px 12px; font-size:14px; outline:none; }
  input:focus, select:focus { border-color:var(--cyan); }
  button { background:var(--cyan); color:#070B12; border:none; border-radius:10px; padding:10px 14px; font-weight:700; font-size:14px; cursor:pointer; }
  button.secondary { background:var(--surface); color:var(--text); border:1px solid var(--border); }
  button.danger { background:rgba(255,82,82,.15); color:var(--red); border:1px solid rgba(255,82,82,.4); padding:6px 10px; font-size:12px; }
  .list-item { display:flex; justify-content:space-between; align-items:center; gap:10px; background:var(--surface); border:1px solid var(--border); border-radius:12px; padding:10px 12px; }
  .toast { position:fixed; bottom:20px; right:20px; background:var(--card); border:1px solid var(--cyan); color:var(--text); padding:12px 16px; border-radius:12px; display:none; max-width:340px; font-size:13px; box-shadow:0 12px 30px rgba(0,0,0,.5); }
</style>
</head>
<body>
<div class="wrap">
  <div class="header">
    <div class="brand">
      <h1>JHE HAKAR • Web Yönetim Paneli</h1>
      <p id="activeInfo">Bağlanıyor...</p>
    </div>
    <span class="badge" id="statusBadge">CANLI BAĞLANTI</span>
  </div>

  <div class="stats">
    <div class="stat"><div class="label">Canlı Kanallar</div><div class="val" id="liveCount">0</div></div>
    <div class="stat"><div class="label">Filmler (VOD)</div><div class="val" id="vodCount">0</div></div>
    <div class="stat"><div class="label">Diziler</div><div class="val" id="seriesCount">0</div></div>
    <div class="stat"><div class="label">Özel / Web Kanallar</div><div class="val" id="customCount">0</div></div>
  </div>

  <div class="grid">
    <div class="card">
      <h2>Xtream IPTV Panel Ekle / Bağla</h2>
      <p class="sub">TV kumandasıyla yazmadan doğrudan buradan panel bağlayın</p>
      <input id="xName" placeholder="Panel Adı (Örn: Ev TV)">
      <input id="xUrl" placeholder="Sunucu URL (http://domain:8080)">
      <input id="xUser" placeholder="Kullanıcı Adı">
      <input id="xPass" type="password" placeholder="Şifre">
      <div style="display:flex;gap:8px">
        <button style="flex:1" onclick="connectXtream()">Bağlan & Senkronize Et</button>
        <button class="secondary" onclick="syncNow()">Yenile</button>
      </div>
    </div>

    <div class="card">
      <h2>TV'de Anında Yayın Aç</h2>
      <p class="sub">Herhangi bir .m3u8 / .mp4 canlı yayın linkini TV oynatıcısına gönderin</p>
      <input id="pTitle" placeholder="Yayın Başlığı">
      <input id="pUrl" placeholder="https://.../playlist.m3u8">
      <button onclick="playOnTv()">TV'de Oynat</button>

      <h2 style="margin-top:8px">Tema & Dil Kontrolü</h2>
      <select id="themeSelect" onchange="saveSettings()">
        <option value="cyber_obsidian">Siber Obsidyen</option>
        <option value="royal_amethyst">Kraliyet Ametist</option>
        <option value="emerald_stadium">Zümrüt Stadyum</option>
        <option value="crimson_cinema">Kızıl Sinema</option>
        <option value="golden_onyx">Altın Oniks</option>
        <option value="ocean_cobalt">Okyanus Kobalt</option>
      </select>
      <select id="langSelect" onchange="saveSettings()">
        <option value="tr">Türkçe</option>
        <option value="ckb">کوردی (سۆرانی)</option>
        <option value="badini">کوردی (بادینی)</option>
        <option value="ar">العربية</option>
        <option value="en">English</option>
      </select>
    </div>

    <div class="card">
      <h2>Ücretsiz / Özel Kanal Ekle</h2>
      <p class="sub">Eklediğiniz kanallar TV'deki Kürtçe TV / Ücretsiz Kanallar listesine eklenir</p>
      <input id="cName" placeholder="Kanal Adı">
      <input id="cUrl" placeholder="Yayın Linki (.m3u8 / .ts)">
      <input id="cCat" placeholder="Kategori (Örn: Haber, Spor)">
      <select id="cDialect">
        <option value="zaxo">Zaxo / Badînan</option>
        <option value="sorani">Soranî</option>
        <option value="kurmanji">Kurmancî / Badînî</option>
        <option value="general">Genel</option>
      </select>
      <button onclick="addCustomChannel()">Kanala Ekle</button>
      <div id="customList" style="display:grid;gap:8px;margin-top:6px"></div>
    </div>

    <div class="card">
      <h2>Kayıtlı Xtream Panelleri & Dış Web Sitesi</h2>
      <p class="sub">Paneller arası geçiş yapın veya harici bir JSON/M3U sitesini bağlayın</p>
      <div id="profileList" style="display:grid;gap:8px"></div>
      <input id="remoteSiteUrl" placeholder="Harici Site URL (https://siteniz.com/panel.json)">
      <button class="secondary" onclick="syncRemoteSite()">Dış Web Sitesinden Çek</button>
    </div>
  </div>
</div>
<div class="toast" id="toast"></div>
<script>
function showToast(msg, isErr) {
  const t = document.getElementById('toast');
  t.style.borderColor = isErr ? '#FF5252' : '#00E5FF';
  t.textContent = msg;
  t.style.display = 'block';
  setTimeout(() => { t.style.display = 'none'; }, 3500);
}
async function postJson(url, data) {
  const res = await fetch(url, { method:'POST', headers:{'Content-Type':'application/json'}, body:JSON.stringify(data||{}) });
  const json = await res.json();
  if (!res.ok || !json.ok) throw new Error(json.error || 'İşlem başarısız');
  showToast(json.message || 'Başarılı', false);
  setTimeout(loadState, 600);
}
async function loadState() {
  try {
    const res = await fetch('/api/state');
    const s = await res.json();
    document.getElementById('liveCount').textContent = s.liveCount;
    document.getElementById('vodCount').textContent = s.vodCount;
    document.getElementById('seriesCount').textContent = s.seriesCount;
    document.getElementById('customCount').textContent = (s.customChannels || []).length;
    document.getElementById('themeSelect').value = s.themeId;
    document.getElementById('langSelect').value = s.languageCode;
    if (document.activeElement.id !== 'remoteSiteUrl') {
      document.getElementById('remoteSiteUrl').value = s.remoteWebsiteUrl || '';
    }
    document.getElementById('activeInfo').textContent = s.activeProfile
      ? ('Aktif Panel: ' + s.activeProfile.name + ' (' + s.activeProfile.username + ')')
      : 'Aktif Xtream Paneli Yok (Ücretsiz mod)';

    const pList = document.getElementById('profileList');
    pList.innerHTML = '';
    (s.profiles || []).forEach(p => {
      const div = document.createElement('div');
      div.className = 'list-item';
      div.innerHTML = '<div><b>' + p.name + '</b><div style="font-size:12px;color:#94A3B8">' + p.serverUrl + '</div></div>';
      if (p.isActive) {
        div.innerHTML += '<span class="badge">AKTİF</span>';
      } else {
        const btn = document.createElement('button');
        btn.className = 'secondary';
        btn.textContent = 'Seç';
        btn.onclick = () => postJson('/api/xtream/switch', { profileId: p.id }).catch(e => showToast(e.message, true));
        div.appendChild(btn);
      }
      pList.appendChild(div);
    });

    const cList = document.getElementById('customList');
    cList.innerHTML = '';
    (s.customChannels || []).forEach(ch => {
      const div = document.createElement('div');
      div.className = 'list-item';
      div.innerHTML = '<div style="overflow:hidden"><b style="display:block">' + ch.name + '</b><span style="font-size:11px;color:#94A3B8">' + ch.category + '</span></div>';
      const del = document.createElement('button');
      del.className = 'danger';
      del.textContent = 'Sil';
      del.onclick = () => postJson('/api/custom-channel/delete', { streamUrl: ch.streamUrl }).catch(e => showToast(e.message, true));
      div.appendChild(del);
      cList.appendChild(div);
    });
  } catch (e) {
    document.getElementById('statusBadge').textContent = 'BAĞLANTI BEKLENİYOR';
  }
}
function connectXtream() {
  postJson('/api/xtream/connect', {
    name: document.getElementById('xName').value,
    serverUrl: document.getElementById('xUrl').value,
    username: document.getElementById('xUser').value,
    password: document.getElementById('xPass').value
  }).catch(e => showToast(e.message, true));
}
function syncNow() { postJson('/api/xtream/sync', {}).catch(e => showToast(e.message, true)); }
function playOnTv() {
  postJson('/api/play', {
    title: document.getElementById('pTitle').value,
    streamUrl: document.getElementById('pUrl').value
  }).catch(e => showToast(e.message, true));
}
function addCustomChannel() {
  postJson('/api/custom-channel/add', {
    name: document.getElementById('cName').value,
    streamUrl: document.getElementById('cUrl').value,
    category: document.getElementById('cCat').value,
    dialect: document.getElementById('cDialect').value
  }).then(() => {
    document.getElementById('cName').value = '';
    document.getElementById('cUrl').value = '';
  }).catch(e => showToast(e.message, true));
}
function saveSettings() {
  postJson('/api/settings', {
    themeId: document.getElementById('themeSelect').value,
    languageCode: document.getElementById('langSelect').value
  }).catch(e => showToast(e.message, true));
}
function syncRemoteSite() {
  postJson('/api/remote-website', {
    websiteUrl: document.getElementById('remoteSiteUrl').value
  }).catch(e => showToast(e.message, true));
}
loadState();
setInterval(loadState, 6000);
</script>
</body>
</html>
""".trimIndent()
    }
}
