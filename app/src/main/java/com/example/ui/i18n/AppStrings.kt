package com.example.ui.i18n

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.LayoutDirection
import com.example.ui.theme.AppThemeOption

enum class AppLanguage(
    val code: String,
    val displayName: String,
    val nativeName: String,
    val isRtl: Boolean,
    val aiLanguagePromptName: String
) {
    KURDISH_SORANI(
        code = "ckb",
        displayName = "Kürtçe (Soranî)",
        nativeName = "کوردی (سۆرانی)",
        isRtl = true,
        aiLanguagePromptName = "Kürtçe Soranî (کوردی سۆرانی)"
    ),
    KURDISH_BADINI(
        code = "badini",
        displayName = "Kürtçe (Badînî)",
        nativeName = "کوردی (بادینی)",
        isRtl = true,
        aiLanguagePromptName = "Kürtçe Badînî (کوردی بادینی)"
    ),
    ENGLISH(
        code = "en",
        displayName = "English",
        nativeName = "English",
        isRtl = false,
        aiLanguagePromptName = "English"
    ),
    ARABIC(
        code = "ar",
        displayName = "Arapça",
        nativeName = "العربية",
        isRtl = true,
        aiLanguagePromptName = "Arapça (العربية)"
    ),
    TURKISH(
        code = "tr",
        displayName = "Türkçe",
        nativeName = "Türkçe",
        isRtl = false,
        aiLanguagePromptName = "Türkçe"
    );

    val layoutDirection: LayoutDirection
        get() = if (isRtl) LayoutDirection.Rtl else LayoutDirection.Ltr

    companion object {
        fun fromCode(code: String?): AppLanguage =
            entries.firstOrNull { it.code == code } ?: TURKISH
    }
}

interface AppStrings {
    // Navigation
    val navHome: String
    val navLive: String
    val navKurdishFree: String
    val navMovies: String
    val navSeries: String
    val navYoutube: String
    val navAi: String
    val navManager: String

    // Home
    val heroBadge: String
    val panelStatusPrefix: String
    val heroSubtitleConnected: String
    val heroSubtitleEmpty: String
    val btnAddXtreamPanel: String
    val btnWatchLive: String
    val btnRefreshCatalog: String
    val btnSyncing: String
    val btnContentManager: String
    val emptyNoPanelTitle: String
    val emptyNoPanelDesc: String
    val emptyNoPanelCta: String
    val statLiveChannels: String
    val statMoviesVod: String
    val statSeries: String
    val statConnections: String
    val statExpiryDate: String
    val unlimited: String
    val unknown: String
    val recentlyWatchedChannels: String
    val recentlyWatchedChannelsSub: String
    val recentlyWatchedEmpty: String
    val continueWatching: String
    val continueWatchingSub: String
    val continueWatchingEmpty: String
    val resumeFromPrefix: String
    val removeFromHistoryDesc: String
    val favoriteChannels: String
    val favoriteChannelsSub: String
    val allChannelsBtn: String
    val featuredMovies: String
    val featuredMoviesSub: String
    val allMoviesBtn: String
    val popularSeries: String
    val popularSeriesSub: String
    val allSeriesBtn: String
    val seriesBadge: String

    // Live TV
    val livePanelRequiredTitle: String
    val livePanelRequiredDesc: String
    val addPanelBtn: String
    val categoriesTitle: String
    val allChannelsTitle: String
    val searchChannelHint: String
    val favoritesLabel: String
    val channelCountSuffix: String
    val noChannelFoundTitle: String
    val noChannelFoundDesc: String
    val liveBadge: String

    // Movies
    val moviesPanelRequiredTitle: String
    val moviesPanelRequiredDesc: String
    val movieCategoriesTitle: String
    val allMoviesTitle: String
    val searchMovieHint: String
    val sortDefault: String
    val sortNameAsc: String
    val sortRatingDesc: String
    val sortRecentDesc: String
    val noMovieFoundTitle: String
    val noMovieFoundDesc: String
    val loadingMovieDetails: String
    val noPlotAvailable: String
    val directorLabel: String
    val castLabel: String
    val playMovieBtn: String
    val inFavoritesBtn: String
    val addToFavoritesBtn: String
    val closeBtn: String

    // Series
    val seriesPanelRequiredTitle: String
    val seriesPanelRequiredDesc: String
    val seriesCategoriesTitle: String
    val allSeriesTitle: String
    val searchSeriesHint: String
    val noSeriesFoundTitle: String
    val noSeriesFoundDesc: String
    val noSeriesDescAvailable: String
    val loadingSeasons: String
    val noEpisodesAvailable: String
    val seasonSuffix: String
    val durationPrefix: String
    val favoriteShort: String

    // AI Guide
    val aiTitle: String
    val aiCatalogSummaryPrefix: String
    val aiSubtitle: String
    val aiQuickPrompts: List<String>
    val aiInputPlaceholder: String
    val aiSendBtn: String
    val aiIdleTitle: String
    val aiIdleDesc: String
    val aiLoadingText: String
    val aiStreamingTitle: String
    val aiSuccessTitle: String
    val aiClearBtn: String
    val aiQuestionPrefix: String
    val aiErrorTitle: String
    val aiRetryBtn: String

    // Content Manager & Language
    val languageSectionTitle: String
    val languageSectionSub: String
    val xtreamConnectTitle: String
    val xtreamEditTitle: String
    val xtreamConnectSub: String
    val smartPasteLabel: String
    val parseBtn: String
    val parseSuccessMsg: String
    val parseFailMsg: String
    val panelNameLabel: String
    val serverUrlLabel: String
    val usernameLabel: String
    val passwordLabel: String
    val connectAndSyncBtn: String
    val connectingBtn: String
    val cancelBtn: String
    val fillSanazBtn: String
    val sanazFilledMsg: String
    val savedPanelsTitle: String
    val savedPanelsSub: String
    val syncNowBtn: String
    val selectBtn: String
    val statusPrefix: String
    val expiryPrefix: String
    val connectionsPrefix: String
    val playerSettingsTitle: String
    val playerSettingsSub: String
    val clearHistoryBtn: String
    val categoryOrganizerTitle: String
    val categoryOrganizerSub: String
    val showAllBtn: String
    val hideAllBtn: String
    val filterCategoryHint: String
    val visibleLabel: String
    val hiddenLabel: String
    val pinnedLabel: String

    // Player
    val resizeFit: String
    val resizeZoom: String
    val resizeFill: String
    val playerErrorTitle: String
    val playerBackBtn: String
    val epgNowPrefix: String
    val epgNextPrefix: String
    val playerAudioBtn: String
    val playerAudioTracksTitle: String
    val playerAudioAutoOption: String
    val playerAudioNoneLabel: String
    val playerAudioDecoderAuto: String
    val playerAudioDecoderFfmpeg: String

    // Parental Control
    val parentalSectionTitle: String
    val parentalSectionSub: String
    val parentalStatusActiveLocked: String
    val parentalStatusActiveUnlocked: String
    val parentalStatusDisabled: String
    val parentalSetupPinBtn: String
    val parentalUnlockBtn: String
    val parentalLockBtn: String
    val parentalChangePinBtn: String
    val parentalDisableBtn: String
    val parentalEnableBtn: String
    val parentalPinDialogCreateTitle: String
    val parentalPinDialogUnlockTitle: String
    val parentalPinDialogChangeTitle: String
    val parentalPinLabel: String
    val parentalPinConfirmLabel: String
    val parentalPinInvalidError: String
    val parentalPinMismatchError: String
    val parentalPinWrongError: String
    val parentalSaveBtn: String
    val parentalChannelHidingTitle: String
    val parentalChannelHidingSub: String
    val parentalFilterAllChannels: String
    val parentalFilterHiddenChannels: String
    val parentalUnhideAllBtn: String
    val parentalHideChannelAction: String
    val parentalShowChannelAction: String
    val parentalLockedNotice: String
    val parentalHiddenCountSuffix: String

    // YouTube TV Section
    val youtubeTitle: String
    val youtubeSubtitle: String
    val youtubeSearchHint: String
    val youtubeGoBtn: String
    val youtubeModeTvBtn: String
    val youtubeModeWebBtn: String
    val youtubeCatHome: String
    val youtubeCatLive: String
    val youtubeCatMusic: String
    val youtubeCatNews: String
    val youtubeCatSports: String
    val youtubeBackBtn: String
    val youtubeReloadBtn: String
    val youtubeOpenAppBtn: String
    val youtubeSaveBookmarkBtn: String
    val youtubeBookmarksTitle: String
    val youtubeAppNotFoundMsg: String

    // Themes
    val themeSectionTitle: String
    val themeSectionSub: String
    val themeBtnLabel: String
    val themeActiveBadge: String
    val themeNameCyber: String
    val themeNameAmethyst: String
    val themeNameEmerald: String
    val themeNameCrimson: String
    val themeNameGolden: String
    val themeNameCobalt: String

    // First-Launch Activation
    val activationTitle: String
    val activationSubtitle: String
    val activationCodeLabel: String
    val activationCodeHint: String
    val activationSubmitBtn: String
    val activationInvalidError: String
    val activationClearBtn: String
    val activationOneTimeNote: String

    // Free Kurdish TV
    val kurdishFreeTitle: String
    val kurdishFreeSubtitle: String
    val kurdishFreeBadge: String
    val kurdishFreeRefreshBtn: String
    val kurdishFreeLoading: String
    val kurdishFreeErrorTitle: String
    val kurdishFreeRetryBtn: String
    val kurdishFreeEmptyTitle: String
    val kurdishFreeEmptyDesc: String
    val kurdishDialectAll: String
    val kurdishDialectZaxo: String
    val kurdishDialectSorani: String
    val kurdishDialectKurmanji: String
    val kurdishDialectGeneral: String

    // Web Control Panel & Website Link
    val webPanelSectionTitle: String
    val webPanelSectionSub: String
    val webPanelLocalTitle: String
    val webPanelLocalRunning: String
    val webPanelLocalStopped: String
    val webPanelStartBtn: String
    val webPanelStopBtn: String
    val webPanelCopyUrlBtn: String
    val webPanelOpenBrowserBtn: String
    val webPanelUrlCopiedMsg: String
    val webPanelRemoteTitle: String
    val webPanelRemoteSub: String
    val webPanelRemoteUrlLabel: String
    val webPanelConnectSyncBtn: String
    val webPanelDisconnectBtn: String
    val webPanelCopyTemplateBtn: String
    val webPanelTemplateCopiedMsg: String
    val webPanelLastSyncPrefix: String
}

fun AppStrings.themeName(theme: AppThemeOption): String = when (theme) {
    AppThemeOption.CYBER_OBSIDIAN -> themeNameCyber
    AppThemeOption.ROYAL_AMETHYST -> themeNameAmethyst
    AppThemeOption.EMERALD_STADIUM -> themeNameEmerald
    AppThemeOption.CRIMSON_CINEMA -> themeNameCrimson
    AppThemeOption.GOLDEN_ONYX -> themeNameGolden
    AppThemeOption.OCEAN_COBALT -> themeNameCobalt
}

val LocalAppStrings = staticCompositionLocalOf<AppStrings> { TurkishStrings }

fun stringsForLanguage(language: AppLanguage): AppStrings = when (language) {
    AppLanguage.TURKISH -> TurkishStrings
    AppLanguage.ENGLISH -> EnglishStrings
    AppLanguage.ARABIC -> ArabicStrings
    AppLanguage.KURDISH_SORANI -> SoraniStrings
    AppLanguage.KURDISH_BADINI -> BadiniStrings
}

private object EnglishStrings : AppStrings {
    override val navHome = "Home"
    override val navLive = "Live TV"
    override val navKurdishFree = "Kurdish TV"
    override val navMovies = "Movies"
    override val navSeries = "Series"
    override val navYoutube = "YouTube"
    override val navAi = "AI Guide"
    override val navManager = "Manage"

    override val heroBadge = "خالد نجم سليفاني"
    override val panelStatusPrefix = "PANEL"
    override val heroSubtitleConnected = "Live channels, movies, and series organized by categories on one clean TV screen."
    override val heroSubtitleEmpty = "Add your Xtream IPTV panel credentials or M3U/Xtream link to get started."
    override val btnAddXtreamPanel = "Add Xtream Panel"
    override val btnWatchLive = "Watch Live TV"
    override val btnRefreshCatalog = "Refresh Catalog"
    override val btnSyncing = "Syncing..."
    override val btnContentManager = "Content Manager"
    override val emptyNoPanelTitle = "No Xtream Panel Connected Yet"
    override val emptyNoPanelDesc = "Enter your server URL, username, and password in the Manage tab to sync all live channels, movies, and series."
    override val emptyNoPanelCta = "Connect Xtream Code"
    override val statLiveChannels = "Live Channels"
    override val statMoviesVod = "Movies (VOD)"
    override val statSeries = "Series"
    override val statConnections = "Connections"
    override val statExpiryDate = "Expiry Date"
    override val unlimited = "Unlimited"
    override val unknown = "Unknown"
    override val recentlyWatchedChannels = "Recently Watched"
    override val recentlyWatchedChannelsSub = "Last 5 live channels you played"
    override val recentlyWatchedEmpty = "No channels played yet. Your last 5 watched live channels will appear here."
    override val continueWatching = "Continue Watching"
    override val continueWatchingSub = "Recently watched channels, movies, and episodes"
    override val continueWatchingEmpty = "No watched content yet. Channels, movies, and series you play will appear here."
    override val resumeFromPrefix = "Resume"
    override val removeFromHistoryDesc = "Remove from history"
    override val favoriteChannels = "Favorite Live Channels"
    override val favoriteChannelsSub = "One-tap quick broadcast access"
    override val allChannelsBtn = "All Channels"
    override val featuredMovies = "Featured Movies"
    override val featuredMoviesSub = "Latest VOD titles from your categories"
    override val allMoviesBtn = "All Movies"
    override val popularSeries = "Popular Series"
    override val popularSeriesSub = "Watch seamlessly with season and episode tracking"
    override val allSeriesBtn = "All Series"
    override val seriesBadge = "SERIES"

    override val livePanelRequiredTitle = "Panel Required for Live TV"
    override val livePanelRequiredDesc = "Add your Xtream IPTV panel in the Manage tab to browse live channels."
    override val addPanelBtn = "Add Panel"
    override val categoriesTitle = "Categories"
    override val allChannelsTitle = "All Channels"
    override val searchChannelHint = "Search channel..."
    override val favoritesLabel = "Favorites"
    override val channelCountSuffix = "channels"
    override val noChannelFoundTitle = "No Channels Found"
    override val noChannelFoundDesc = "No live channels match the selected category or search query."
    override val liveBadge = "LIVE"

    override val moviesPanelRequiredTitle = "Panel Required for Movies"
    override val moviesPanelRequiredDesc = "Connect your Xtream panel in the Manage tab to browse VOD movies."
    override val movieCategoriesTitle = "Movie Categories"
    override val allMoviesTitle = "All Movies"
    override val searchMovieHint = "Search movie..."
    override val sortDefault = "Default"
    override val sortNameAsc = "By Name (A-Z)"
    override val sortRatingDesc = "Top Rated"
    override val sortRecentDesc = "Recently Added"
    override val noMovieFoundTitle = "No Movies Found"
    override val noMovieFoundDesc = "No movies match the selected category or search query."
    override val loadingMovieDetails = "Loading movie details..."
    override val noPlotAvailable = "No synopsis was provided by the panel for this movie."
    override val directorLabel = "Director"
    override val castLabel = "Cast"
    override val playMovieBtn = "Play Movie"
    override val inFavoritesBtn = "Favorited"
    override val addToFavoritesBtn = "Add to Favorites"
    override val closeBtn = "Close"

    override val seriesPanelRequiredTitle = "Panel Required for Series"
    override val seriesPanelRequiredDesc = "Connect your Xtream panel in the Manage tab to browse series and episodes."
    override val seriesCategoriesTitle = "Series Categories"
    override val allSeriesTitle = "All Series"
    override val searchSeriesHint = "Search series..."
    override val noSeriesFoundTitle = "No Series Found"
    override val noSeriesFoundDesc = "No series match the selected category or search query."
    override val noSeriesDescAvailable = "No description available for this series."
    override val loadingSeasons = "Loading seasons and episodes..."
    override val noEpisodesAvailable = "Could not load episodes for this series."
    override val seasonSuffix = "Season"
    override val durationPrefix = "Duration"
    override val favoriteShort = "Favorite"

    override val aiTitle = "JHE HAKAR AI Guide"
    override val aiCatalogSummaryPrefix = "Catalog summary"
    override val aiSubtitle = "Pick a quick prompt with your TV remote or type what you want to watch."
    override val aiQuickPrompts = listOf(
        "What should I watch tonight from my catalog?",
        "Recommend the best action or thriller movies",
        "How should I organize my Live TV and movie categories?",
        "Suggest family-friendly series and movies"
    )
    override val aiInputPlaceholder = "e.g. Recommend a gripping sci-fi movie..."
    override val aiSendBtn = "Ask"
    override val aiIdleTitle = "Ready for Smart Recommendations"
    override val aiIdleDesc = "Select one of the quick prompt cards above or send your own question."
    override val aiLoadingText = "Gemini is analyzing your catalog..."
    override val aiStreamingTitle = "Generating response..."
    override val aiSuccessTitle = "Gemini Recommendation"
    override val aiClearBtn = "Clear"
    override val aiQuestionPrefix = "Question"
    override val aiErrorTitle = "Could Not Get AI Response"
    override val aiRetryBtn = "Retry"

    override val languageSectionTitle = "App Language"
    override val languageSectionSub = "Switch instantly between Turkish, English, Arabic, Kurdish Sorani, and Kurdish Badini"
    override val xtreamConnectTitle = "Xtream IPTV Panel Connection"
    override val xtreamEditTitle = "Edit Panel Details"
    override val xtreamConnectSub = "Paste an Xtream link directly or enter server details manually"
    override val smartPasteLabel = "Smart Paste (M3U / Xtream Link or Credentials Text)"
    override val parseBtn = "Parse"
    override val parseSuccessMsg = "Xtream credentials auto-filled!"
    override val parseFailMsg = "Could not parse link; please fill the fields manually."
    override val panelNameLabel = "Panel Name (e.g. Home TV)"
    override val serverUrlLabel = "Server URL (URL:Port)"
    override val usernameLabel = "Username"
    override val passwordLabel = "Password"
    override val connectAndSyncBtn = "Connect & Sync"
    override val connectingBtn = "Connecting..."
    override val cancelBtn = "Cancel"
    override val fillSanazBtn = "Fill SANAZ Preset"
    override val sanazFilledMsg = "SANAZ panel details filled into the form."
    override val savedPanelsTitle = "Saved Xtream Panels"
    override val savedPanelsSub = "Switch panels with one tap or refresh your catalog"
    override val syncNowBtn = "Sync Now"
    override val selectBtn = "Select"
    override val statusPrefix = "Status"
    override val expiryPrefix = "Expires"
    override val connectionsPrefix = "Connections"
    override val playerSettingsTitle = "Player & Stream Settings"
    override val playerSettingsSub = "Select live stream format (HLS .m3u8 recommended for most TV Boxes)"
    override val clearHistoryBtn = "Clear History"
    override val categoryOrganizerTitle = "Category & Content Organizer"
    override val categoryOrganizerSub = "Hide unwanted categories, pin favorites to the top, or reorder"
    override val showAllBtn = "Show All"
    override val hideAllBtn = "Hide All"
    override val filterCategoryHint = "Filter category name..."
    override val visibleLabel = "Visible"
    override val hiddenLabel = "Hidden"
    override val pinnedLabel = "Pinned"

    override val resizeFit = "Fit"
    override val resizeZoom = "Zoom"
    override val resizeFill = "Fill"
    override val playerErrorTitle = "Playback Error"
    override val playerBackBtn = "Go Back"
    override val epgNowPrefix = "Now"
    override val epgNextPrefix = "Next"
    override val playerAudioBtn = "Audio"
    override val playerAudioTracksTitle = "Audio Track & Sound"
    override val playerAudioAutoOption = "Auto (Recommended)"
    override val playerAudioNoneLabel = "No audio track detected"
    override val playerAudioDecoderAuto = "Audio: Auto"
    override val playerAudioDecoderFfmpeg = "Audio: FFmpeg"

    override val parentalSectionTitle = "Parental Control (PIN)"
    override val parentalSectionSub = "Protect and hide specific channels or categories with a PIN"
    override val parentalStatusActiveLocked = "Active • Locked"
    override val parentalStatusActiveUnlocked = "Active • Unlocked"
    override val parentalStatusDisabled = "Disabled"
    override val parentalSetupPinBtn = "Set PIN & Enable"
    override val parentalUnlockBtn = "Unlock with PIN"
    override val parentalLockBtn = "Lock Now"
    override val parentalChangePinBtn = "Change PIN"
    override val parentalDisableBtn = "Disable"
    override val parentalEnableBtn = "Enable"
    override val parentalPinDialogCreateTitle = "Create Parental PIN"
    override val parentalPinDialogUnlockTitle = "Enter Parental PIN"
    override val parentalPinDialogChangeTitle = "Change Parental PIN"
    override val parentalPinLabel = "PIN (4-8 digits)"
    override val parentalPinConfirmLabel = "Confirm PIN"
    override val parentalPinInvalidError = "PIN must be 4 to 8 digits."
    override val parentalPinMismatchError = "PINs do not match."
    override val parentalPinWrongError = "Incorrect PIN."
    override val parentalSaveBtn = "Save"
    override val parentalChannelHidingTitle = "Channel Visibility & Hiding"
    override val parentalChannelHidingSub = "Hide or unhide specific live channels"
    override val parentalFilterAllChannels = "All"
    override val parentalFilterHiddenChannels = "Hidden"
    override val parentalUnhideAllBtn = "Show All Channels"
    override val parentalHideChannelAction = "Hide channel"
    override val parentalShowChannelAction = "Unhide channel"
    override val parentalLockedNotice = "Enter Parental PIN to modify hidden channels or categories."
    override val parentalHiddenCountSuffix = "hidden"

    override val youtubeTitle = "YouTube Smart TV"
    override val youtubeSubtitle = "D-Pad TV mode, fast search, live streams, and saved channels"
    override val youtubeSearchHint = "Search YouTube or paste video URL..."
    override val youtubeGoBtn = "Search"
    override val youtubeModeTvBtn = "TV Mode"
    override val youtubeModeWebBtn = "Web Mode"
    override val youtubeCatHome = "Home"
    override val youtubeCatLive = "Live"
    override val youtubeCatMusic = "Music"
    override val youtubeCatNews = "News"
    override val youtubeCatSports = "Sports"
    override val youtubeBackBtn = "Back"
    override val youtubeReloadBtn = "Reload"
    override val youtubeOpenAppBtn = "Open in TV App"
    override val youtubeSaveBookmarkBtn = "Pin"
    override val youtubeBookmarksTitle = "Pinned Shortcuts"
    override val youtubeAppNotFoundMsg = "No external YouTube app found; continuing inside built-in player."

    override val themeSectionTitle = "App Theme & Appearance"
    override val themeSectionSub = "Choose your preferred Smart TV color atmosphere"
    override val themeBtnLabel = "Theme"
    override val themeActiveBadge = "Active"
    override val themeNameCyber = "Cyber Obsidian"
    override val themeNameAmethyst = "Royal Amethyst"
    override val themeNameEmerald = "Emerald Stadium"
    override val themeNameCrimson = "Crimson Cinema"
    override val themeNameGolden = "Golden Onyx"
    override val themeNameCobalt = "Ocean Cobalt"

    override val activationTitle = "App Activation Required"
    override val activationSubtitle = "Enter the 4-digit activation code to unlock JHE HAKAR on this device."
    override val activationCodeLabel = "Activation Code"
    override val activationCodeHint = "Enter 4-digit code"
    override val activationSubmitBtn = "Activate & Start"
    override val activationInvalidError = "Invalid activation code. Please check and try again."
    override val activationClearBtn = "Clear"
    override val activationOneTimeNote = "One-time setup required only on first installation."

    override val kurdishFreeTitle = "Free Kurdish Channels"
    override val kurdishFreeSubtitle = "100% free public Kurdish live broadcasts — Sorani, Kurmanji & Badini"
    override val kurdishFreeBadge = "FREE"
    override val kurdishFreeRefreshBtn = "Refresh"
    override val kurdishFreeLoading = "Loading free Kurdish channels..."
    override val kurdishFreeErrorTitle = "Could Not Load Free Channels"
    override val kurdishFreeRetryBtn = "Retry"
    override val kurdishFreeEmptyTitle = "No Kurdish Channels Found"
    override val kurdishFreeEmptyDesc = "No free Kurdish channels match your selected dialect or search."
    override val kurdishDialectAll = "All"
    override val kurdishDialectZaxo = "Zakho / Badînan"
    override val kurdishDialectSorani = "Sorani"
    override val kurdishDialectKurmanji = "Kurmanji / Badini"
    override val kurdishDialectGeneral = "General Kurdish"

    override val webPanelSectionTitle = "Website & Browser Control Panel"
    override val webPanelSectionSub = "Manage this app from a phone/PC browser or link an external website panel"
    override val webPanelLocalTitle = "Built-in Web Panel (Same Wi-Fi)"
    override val webPanelLocalRunning = "Active"
    override val webPanelLocalStopped = "Stopped"
    override val webPanelStartBtn = "Start Web Panel"
    override val webPanelStopBtn = "Stop"
    override val webPanelCopyUrlBtn = "Copy URL"
    override val webPanelOpenBrowserBtn = "Open in Browser"
    override val webPanelUrlCopiedMsg = "Web panel URL copied to clipboard."
    override val webPanelRemoteTitle = "External Website Panel (JSON / M3U)"
    override val webPanelRemoteSub = "Enter your website URL to sync channels, Xtream portals, and announcements"
    override val webPanelRemoteUrlLabel = "Website Panel URL (https://...)"
    override val webPanelConnectSyncBtn = "Connect & Sync Site"
    override val webPanelDisconnectBtn = "Disconnect"
    override val webPanelCopyTemplateBtn = "Copy JSON Template"
    override val webPanelTemplateCopiedMsg = "Website JSON panel template copied to clipboard."
    override val webPanelLastSyncPrefix = "Last Sync"
}

private object TurkishStrings : AppStrings {
    override val navHome = "Ana Sayfa"
    override val navLive = "Canlı TV"
    override val navKurdishFree = "Kürtçe TV"
    override val navMovies = "Filmler"
    override val navSeries = "Diziler"
    override val navYoutube = "YouTube"
    override val navAi = "AI Rehber"
    override val navManager = "Yönetim"

    override val heroBadge = "خالد نجم سليفاني"
    override val panelStatusPrefix = "PANEL"
    override val heroSubtitleConnected = "Kategorilerle düzenlenmiş canlı kanallar, filmler ve diziler tek ekranda."
    override val heroSubtitleEmpty = "Başlamak için Xtream IPTV panel bilgilerinizi veya M3U/Xtream bağlantınızı ekleyin."
    override val btnAddXtreamPanel = "Xtream Panel Ekle"
    override val btnWatchLive = "Canlı TV İzle"
    override val btnRefreshCatalog = "Kataloğu Yenile"
    override val btnSyncing = "Güncelleniyor..."
    override val btnContentManager = "İçerik Yönetimi"
    override val emptyNoPanelTitle = "Henüz Xtream Paneli Bağlanmadı"
    override val emptyNoPanelDesc = "Yönetim sekmesinden sunucu adresi, kullanıcı adı ve şifrenizi girerek tüm kanalları, filmleri ve dizileri senkronize edebilirsiniz."
    override val emptyNoPanelCta = "Xtream Kodu Entegre Et"
    override val statLiveChannels = "Canlı Kanallar"
    override val statMoviesVod = "Filmler (VOD)"
    override val statSeries = "Diziler"
    override val statConnections = "Bağlantı"
    override val statExpiryDate = "Bitiş Tarihi"
    override val unlimited = "Sınırsız"
    override val unknown = "Bilinmiyor"
    override val recentlyWatchedChannels = "Son İzlenen Kanallar"
    override val recentlyWatchedChannelsSub = "Oynattığınız son 5 canlı kanal"
    override val recentlyWatchedEmpty = "Henüz kanal oynatılmadı. İzlediğiniz son 5 canlı kanal burada listelenir."
    override val continueWatching = "Kaldığın Yerden Devam Et"
    override val continueWatchingSub = "Son izlenen kanallar, filmler ve bölümler"
    override val continueWatchingEmpty = "Henüz izlenen içerik yok. Oynattığınız kanallar, filmler ve diziler burada listelenir."
    override val resumeFromPrefix = "Devam et"
    override val removeFromHistoryDesc = "Geçmişten kaldır"
    override val favoriteChannels = "Favori Canlı Kanallar"
    override val favoriteChannelsSub = "Tek dokunuşla hızlı yayın erişimi"
    override val allChannelsBtn = "Tüm Kanallar"
    override val featuredMovies = "Öne Çıkan Filmler"
    override val featuredMoviesSub = "Kategorilerden seçilmiş güncel VOD arşivi"
    override val allMoviesBtn = "Tüm Filmler"
    override val popularSeries = "Popüler Diziler"
    override val popularSeriesSub = "Sezon ve bölüm takibiyle kesintisiz izleyin"
    override val allSeriesBtn = "Tüm Diziler"
    override val seriesBadge = "DİZİ"

    override val livePanelRequiredTitle = "Canlı TV İçin Panel Gerekli"
    override val livePanelRequiredDesc = "Canlı TV kanallarını listelemek için Yönetim sekmesinden Xtream IPTV panelinizi ekleyin."
    override val addPanelBtn = "Panel Ekle"
    override val categoriesTitle = "Kategoriler"
    override val allChannelsTitle = "Tüm Kanallar"
    override val searchChannelHint = "Kanal ara..."
    override val favoritesLabel = "Favoriler"
    override val channelCountSuffix = "kanal"
    override val noChannelFoundTitle = "Kanal Bulunamadı"
    override val noChannelFoundDesc = "Seçili kategori veya arama kriterine uygun canlı kanal yok."
    override val liveBadge = "CANLI"

    override val moviesPanelRequiredTitle = "Film Arşivi İçin Panel Gerekli"
    override val moviesPanelRequiredDesc = "VOD filmlerinizi izlemek için Yönetim sekmesinden Xtream panelinizi bağlayın."
    override val movieCategoriesTitle = "Film Kategorileri"
    override val allMoviesTitle = "Tüm Filmler"
    override val searchMovieHint = "Film ara..."
    override val sortDefault = "Varsayılan"
    override val sortNameAsc = "İsme Göre (A-Z)"
    override val sortRatingDesc = "Puana Göre"
    override val sortRecentDesc = "En Son Eklenen"
    override val noMovieFoundTitle = "Film Bulunamadı"
    override val noMovieFoundDesc = "Seçili kategori veya arama kriterine uygun film bulunmuyor."
    override val loadingMovieDetails = "Film detayları yükleniyor..."
    override val noPlotAvailable = "Bu film için özet bilgisi panel tarafından sağlanmadı."
    override val directorLabel = "Yönetmen"
    override val castLabel = "Oyuncular"
    override val playMovieBtn = "Filmi Oynat"
    override val inFavoritesBtn = "Favoride"
    override val addToFavoritesBtn = "Favoriye Ekle"
    override val closeBtn = "Kapat"

    override val seriesPanelRequiredTitle = "Diziler İçin Panel Gerekli"
    override val seriesPanelRequiredDesc = "Sezon ve bölüm takibiyle dizileri izlemek için Yönetim sekmesinden Xtream panelinizi bağlayın."
    override val seriesCategoriesTitle = "Dizi Kategorileri"
    override val allSeriesTitle = "Tüm Diziler"
    override val searchSeriesHint = "Dizi ara..."
    override val noSeriesFoundTitle = "Dizi Bulunamadı"
    override val noSeriesFoundDesc = "Seçili kategori veya arama kriterine uygun dizi yok."
    override val noSeriesDescAvailable = "Bu dizi için açıklama bilgisi bulunmuyor."
    override val loadingSeasons = "Sezonlar ve bölümler yükleniyor..."
    override val noEpisodesAvailable = "Bu dizi için bölüm listesi alınamadı."
    override val seasonSuffix = "Sezon"
    override val durationPrefix = "Süre"
    override val favoriteShort = "Favori"

    override val aiTitle = "JHE HAKAR AI Rehber"
    override val aiCatalogSummaryPrefix = "Katalog özeti"
    override val aiSubtitle = "TV kumandanızla hızlı sorulardan birini seçin veya izlemek istediğiniz türü yazın."
    override val aiQuickPrompts = listOf(
        "Bu akşam kataloğumdan ne izlemeliyim?",
        "En iyi aksiyon veya gerilim filmlerini öner",
        "Canlı TV ve film kategorilerimi nasıl düzenlemeliyim?",
        "Ailece izlenebilecek dizi ve film önerileri ver"
    )
    override val aiInputPlaceholder = "Örn: Bugün sürükleyici bir bilim kurgu filmi öner..."
    override val aiSendBtn = "Sor"
    override val aiIdleTitle = "Akıllı Önerilere Hazır"
    override val aiIdleDesc = "Yukarıdaki öneri kartlarından birine basın veya kendi sorunuzu gönderin."
    override val aiLoadingText = "Gemini kataloğunuzu analiz ediyor..."
    override val aiStreamingTitle = "Yanıt oluşturuluyor..."
    override val aiSuccessTitle = "Gemini Önerisi"
    override val aiClearBtn = "Temizle"
    override val aiQuestionPrefix = "Soru"
    override val aiErrorTitle = "AI Yanıtı Alınamadı"
    override val aiRetryBtn = "Tekrar Dene"

    override val languageSectionTitle = "Uygulama Dili / Language"
    override val languageSectionSub = "Türkçe, Arapça ve Kürtçe (Soranî / Badînî) arasında anında geçiş yapın"
    override val xtreamConnectTitle = "Xtream IPTV Panel Bağlantısı"
    override val xtreamEditTitle = "Panel Bilgilerini Düzenle"
    override val xtreamConnectSub = "Xtream kodunu doğrudan yapıştırın veya sunucu bilgilerini tek tek girin"
    override val smartPasteLabel = "Hızlı Yapıştır (M3U / Xtream Linki veya Mesaj Metni)"
    override val parseBtn = "Ayrıştır"
    override val parseSuccessMsg = "Xtream bilgileri otomatik dolduruldu!"
    override val parseFailMsg = "Bağlantı ayrıştırılamadı, alanları manuel doldurabilirsiniz."
    override val panelNameLabel = "Panel Adı (Örn: Ev TV)"
    override val serverUrlLabel = "Sunucu Adresi (URL:Port)"
    override val usernameLabel = "Kullanıcı Adı"
    override val passwordLabel = "Şifre"
    override val connectAndSyncBtn = "Bağlan ve Senkronize Et"
    override val connectingBtn = "Bağlanıyor..."
    override val cancelBtn = "İptal"
    override val fillSanazBtn = "SANAZ Bilgilerini Doldur"
    override val sanazFilledMsg = "SANAZ panel bilgileri forma dolduruldu."
    override val savedPanelsTitle = "Kayıtlı Xtream Panelleri"
    override val savedPanelsSub = "Paneller arasında tek tıkla geçiş yapın veya kataloğu güncelleyin"
    override val syncNowBtn = "Senkronize Et"
    override val selectBtn = "Seç"
    override val statusPrefix = "Durum"
    override val expiryPrefix = "Bitiş"
    override val connectionsPrefix = "Bağlantı"
    override val playerSettingsTitle = "Oynatıcı ve Akış Ayarları"
    override val playerSettingsSub = "Canlı yayın protokolünü seçin (HLS .m3u8 çoğu TV Box için önerilir)"
    override val clearHistoryBtn = "Geçmişi Sil"
    override val categoryOrganizerTitle = "Kategori ve İçerik Düzenleyici"
    override val categoryOrganizerSub = "İstemediğiniz kategorileri gizleyin, sık kullandıklarınızı başa sabitleyin"
    override val showAllBtn = "Tümünü Göster"
    override val hideAllBtn = "Tümünü Gizle"
    override val filterCategoryHint = "Kategori adı filtrele..."
    override val visibleLabel = "Görünür"
    override val hiddenLabel = "Gizli"
    override val pinnedLabel = "Sabitlendi"

    override val resizeFit = "Sığdır"
    override val resizeZoom = "Yakınlaştır"
    override val resizeFill = "Tam Ekran"
    override val playerErrorTitle = "Yayın Hatası"
    override val playerBackBtn = "Geri Dön"
    override val epgNowPrefix = "Şu An"
    override val epgNextPrefix = "Sonraki"
    override val playerAudioBtn = "Ses"
    override val playerAudioTracksTitle = "Ses İzi ve Çözücü"
    override val playerAudioAutoOption = "Otomatik (Önerilen)"
    override val playerAudioNoneLabel = "Yayında ses izi algılanmadı"
    override val playerAudioDecoderAuto = "Ses: Oto"
    override val playerAudioDecoderFfmpeg = "Ses: FFmpeg"

    override val parentalSectionTitle = "Ebeveyn Kontrolü (PIN)"
    override val parentalSectionSub = "Belirli kanalları veya kategorileri PIN koruması ile gizleyin"
    override val parentalStatusActiveLocked = "Aktif • Kilitli"
    override val parentalStatusActiveUnlocked = "Aktif • Kilit Açık"
    override val parentalStatusDisabled = "Kapalı"
    override val parentalSetupPinBtn = "PIN Oluştur ve Etkinleştir"
    override val parentalUnlockBtn = "PIN ile Kilidi Aç"
    override val parentalLockBtn = "Kilitle"
    override val parentalChangePinBtn = "PIN Değiştir"
    override val parentalDisableBtn = "Devre Dışı Bırak"
    override val parentalEnableBtn = "Etkinleştir"
    override val parentalPinDialogCreateTitle = "Ebeveyn PIN'i Oluştur"
    override val parentalPinDialogUnlockTitle = "Ebeveyn PIN'ini Girin"
    override val parentalPinDialogChangeTitle = "Ebeveyn PIN'ini Değiştir"
    override val parentalPinLabel = "PIN (4-8 rakam)"
    override val parentalPinConfirmLabel = "PIN Tekrar"
    override val parentalPinInvalidError = "PIN 4 ile 8 rakam arasında olmalıdır."
    override val parentalPinMismatchError = "Girilen PIN kodları eşleşmiyor."
    override val parentalPinWrongError = "Ebeveyn PIN'i hatalı."
    override val parentalSaveBtn = "Kaydet"
    override val parentalChannelHidingTitle = "Kanal Gizleme ve Görünürlük"
    override val parentalChannelHidingSub = "Belirli canlı TV kanallarını listeden gizleyin veya açın"
    override val parentalFilterAllChannels = "Tümü"
    override val parentalFilterHiddenChannels = "Gizlenenler"
    override val parentalUnhideAllBtn = "Tüm Kanalları Göster"
    override val parentalHideChannelAction = "Kanalı gizle"
    override val parentalShowChannelAction = "Kanalı göster"
    override val parentalLockedNotice = "Gizli kanalları veya kategorileri düzenlemek için PIN kilidini açın."
    override val parentalHiddenCountSuffix = "gizli"

    override val youtubeTitle = "YouTube Smart TV"
    override val youtubeSubtitle = "TV kumanda modu, hızlı arama, canlı yayınlar ve sabitlenen kanallar"
    override val youtubeSearchHint = "YouTube'da ara veya video linki yapıştır..."
    override val youtubeGoBtn = "Ara"
    override val youtubeModeTvBtn = "TV Modu"
    override val youtubeModeWebBtn = "Web Modu"
    override val youtubeCatHome = "Ana Sayfa"
    override val youtubeCatLive = "Canlı"
    override val youtubeCatMusic = "Müzik"
    override val youtubeCatNews = "Haber"
    override val youtubeCatSports = "Spor"
    override val youtubeBackBtn = "Geri"
    override val youtubeReloadBtn = "Yenile"
    override val youtubeOpenAppBtn = "Uygulamada Aç"
    override val youtubeSaveBookmarkBtn = "Sabitle"
    override val youtubeBookmarksTitle = "Sabitlenenler"
    override val youtubeAppNotFoundMsg = "Harici YouTube uygulaması bulunamadı; dahili oynatıcıda devam ediliyor."

    override val themeSectionTitle = "Uygulama Teması ve Görünüm"
    override val themeSectionSub = "Smart TV arayüzü için renk atmosferinizi seçin"
    override val themeBtnLabel = "Tema"
    override val themeActiveBadge = "Aktif"
    override val themeNameCyber = "Siber Obsidyen"
    override val themeNameAmethyst = "Kraliyet Ametist"
    override val themeNameEmerald = "Zümrüt Stadyum"
    override val themeNameCrimson = "Kızıl Sinema"
    override val themeNameGolden = "Altın Oniks"
    override val themeNameCobalt = "Okyanus Kobalt"

    override val activationTitle = "Uygulama Aktivasyonu Gerekli"
    override val activationSubtitle = "JHE HAKAR uygulamasını bu cihazda açmak için 4 haneli aktivasyon kodunu girin."
    override val activationCodeLabel = "Aktivasyon Kodu"
    override val activationCodeHint = "4 haneli kodu girin"
    override val activationSubmitBtn = "Aktifleştir ve Başlat"
    override val activationInvalidError = "Hatalı aktivasyon kodu. Lütfen kontrol edip tekrar deneyin."
    override val activationClearBtn = "Temizle"
    override val activationOneTimeNote = "Bu doğrulama yalnızca ilk kurulumda bir kez istenir."

    override val kurdishFreeTitle = "Ücretsiz Kürtçe Kanallar"
    override val kurdishFreeSubtitle = "Üyelik veya panel gerektirmeyen %100 bedava canlı Kürtçe TV yayınları"
    override val kurdishFreeBadge = "BEDAVA"
    override val kurdishFreeRefreshBtn = "Yenile"
    override val kurdishFreeLoading = "Ücretsiz Kürtçe kanallar yükleniyor..."
    override val kurdishFreeErrorTitle = "Ücretsiz Kanallar Yüklenemedi"
    override val kurdishFreeRetryBtn = "Tekrar Dene"
    override val kurdishFreeEmptyTitle = "Kanal Bulunamadı"
    override val kurdishFreeEmptyDesc = "Seçili lehçe veya arama kriterine uygun ücretsiz Kürtçe kanal yok."
    override val kurdishDialectAll = "Tümü"
    override val kurdishDialectZaxo = "Zaxo / Badînan"
    override val kurdishDialectSorani = "Soranî"
    override val kurdishDialectKurmanji = "Kurmancî / Badînî"
    override val kurdishDialectGeneral = "Genel Kürtçe"

    override val webPanelSectionTitle = "Web Sitesi ve Tarayıcı Kontrol Paneli"
    override val webPanelSectionSub = "Uygulamayı telefon/PC tarayıcısından yönetin veya harici bir internet sitesine bağlayın"
    override val webPanelLocalTitle = "Canlı Web Yönetim Paneli (Aynı Wi-Fi)"
    override val webPanelLocalRunning = "Açık"
    override val webPanelLocalStopped = "Kapalı"
    override val webPanelStartBtn = "Web Panelini Başlat"
    override val webPanelStopBtn = "Durdur"
    override val webPanelCopyUrlBtn = "Adresi Kopyala"
    override val webPanelOpenBrowserBtn = "Tarayıcıda Aç"
    override val webPanelUrlCopiedMsg = "Web panel adresi panoya kopyalandı."
    override val webPanelRemoteTitle = "Harici İnternet Sitesi Paneli (JSON / M3U)"
    override val webPanelRemoteSub = "Kendi web sitenizin adresini girin; kanal, Xtream ve duyurular otomatik çekilsin"
    override val webPanelRemoteUrlLabel = "İnternet Sitesi Adresi (https://...)"
    override val webPanelConnectSyncBtn = "Siteye Bağlan ve Çek"
    override val webPanelDisconnectBtn = "Bağlantıyı Kes"
    override val webPanelCopyTemplateBtn = "Site JSON Şablonu Kopyala"
    override val webPanelTemplateCopiedMsg = "Web sitesi JSON panel şablonu panoya kopyalandı."
    override val webPanelLastSyncPrefix = "Son Eşitleme"
}

private object ArabicStrings : AppStrings {
    override val navHome = "الرئيسية"
    override val navLive = "البث المباشر"
    override val navKurdishFree = "كردي TV"
    override val navMovies = "الأفلام"
    override val navSeries = "المسلسلات"
    override val navYoutube = "يوتيوب"
    override val navAi = "دليل AI"
    override val navManager = "الإدارة"

    override val heroBadge = "خالد نجم سليفاني"
    override val panelStatusPrefix = "السيرفر"
    override val heroSubtitleConnected = "قنوات البث المباشر والأفلام والمسلسلات مرتبة حسب الفئات في شاشة واحدة."
    override val heroSubtitleEmpty = "أضف بيانات لوحة Xtream IPTV أو رابط M3U للبدء."
    override val btnAddXtreamPanel = "إضافة سيرفر Xtream"
    override val btnWatchLive = "مشاهدة البث المباشر"
    override val btnRefreshCatalog = "تحديث المحتوى"
    override val btnSyncing = "جاري التحديث..."
    override val btnContentManager = "إدارة المحتوى"
    override val emptyNoPanelTitle = "لم يتم ربط أي سيرفر Xtream بعد"
    override val emptyNoPanelDesc = "من قسم الإدارة، أدخل عنوان السيرفر واسم المستخدم وكلمة المرور لمزامنة القنوات والأفلام والمسلسلات."
    override val emptyNoPanelCta = "ربط كود Xtream"
    override val statLiveChannels = "القنوات المباشرة"
    override val statMoviesVod = "الأفلام (VOD)"
    override val statSeries = "المسلسلات"
    override val statConnections = "الاتصالات"
    override val statExpiryDate = "تاريخ الانتهاء"
    override val unlimited = "غير محدود"
    override val unknown = "غير معروف"
    override val recentlyWatchedChannels = "القنوات المشاهدة مؤخراً"
    override val recentlyWatchedChannelsSub = "آخر 5 قنوات مباشرة قمت بتشغيلها"
    override val recentlyWatchedEmpty = "لم يتم تشغيل أي قناة بعد. ستظهر هنا آخر 5 قنوات مباشرة شاهدتها."
    override val continueWatching = "متابعة المشاهدة"
    override val continueWatchingSub = "آخر القنوات والأفلام والحلقات التي شاهدتها"
    override val continueWatchingEmpty = "لا يوجد محتوى مشاهد بعد. ستظهر هنا القنوات والأفلام والمسلسلات التي تشاهدها."
    override val resumeFromPrefix = "استئناف"
    override val removeFromHistoryDesc = "إزالة من السجل"
    override val favoriteChannels = "القنوات المفضلة"
    override val favoriteChannelsSub = "وصول سريع بنقرة واحدة"
    override val allChannelsBtn = "كل القنوات"
    override val featuredMovies = "أفلام مختارة"
    override val featuredMoviesSub = "أحدث أفلام VOD من فئاتك"
    override val allMoviesBtn = "كل الأفلام"
    override val popularSeries = "مسلسلات شائعة"
    override val popularSeriesSub = "شاهد بانتظام مع تتبع المواسم والحلقات"
    override val allSeriesBtn = "كل المسلسلات"
    override val seriesBadge = "مسلسل"

    override val livePanelRequiredTitle = "مطلوب سيرفر للبث المباشر"
    override val livePanelRequiredDesc = "لعرض القنوات المباشرة، أضف لوحة Xtream IPTV من قسم الإدارة."
    override val addPanelBtn = "إضافة سيرفر"
    override val categoriesTitle = "الفئات"
    override val allChannelsTitle = "جميع القنوات"
    override val searchChannelHint = "ابحث عن قناة..."
    override val favoritesLabel = "المفضلة"
    override val channelCountSuffix = "قناة"
    override val noChannelFoundTitle = "لم يتم العثور على قنوات"
    override val noChannelFoundDesc = "لا توجد قنوات مطابقة للفئة المحددة أو البحث."
    override val liveBadge = "مباشر"

    override val moviesPanelRequiredTitle = "مطلوب سيرفر للأفلام"
    override val moviesPanelRequiredDesc = "لمشاهدة مكتبة الأفلام، قم بربط سيرفر Xtream من قسم الإدارة."
    override val movieCategoriesTitle = "فئات الأفلام"
    override val allMoviesTitle = "جميع الأفلام"
    override val searchMovieHint = "ابحث عن فيلم..."
    override val sortDefault = "الافتراضي"
    override val sortNameAsc = "حسب الاسم (أ-ي)"
    override val sortRatingDesc = "الأعلى تقييماً"
    override val sortRecentDesc = "المضاف حديثاً"
    override val noMovieFoundTitle = "لم يتم العثور على أفلام"
    override val noMovieFoundDesc = "لا توجد أفلام مطابقة للفئة أو البحث."
    override val loadingMovieDetails = "جاري تحميل تفاصيل الفيلم..."
    override val noPlotAvailable = "لا يتوفر وصف لهذا الفيلم من السيرفر."
    override val directorLabel = "المخرج"
    override val castLabel = "الممثلون"
    override val playMovieBtn = "تشغيل الفيلم"
    override val inFavoritesBtn = "في المفضلة"
    override val addToFavoritesBtn = "إضافة للمفضلة"
    override val closeBtn = "إغلاق"

    override val seriesPanelRequiredTitle = "مطلوب سيرفر للمسلسلات"
    override val seriesPanelRequiredDesc = "لمشاهدة المسلسلات والحلقات، قم بربط سيرفر Xtream من قسم الإدارة."
    override val seriesCategoriesTitle = "فئات المسلسلات"
    override val allSeriesTitle = "جميع المسلسلات"
    override val searchSeriesHint = "ابحث عن مسلسل..."
    override val noSeriesFoundTitle = "لم يتم العثور على مسلسلات"
    override val noSeriesFoundDesc = "لا توجد مسلسلات مطابقة للفئة أو البحث."
    override val noSeriesDescAvailable = "لا يتوفر وصف لهذا المسلسل."
    override val loadingSeasons = "جاري تحميل المواسم والحلقات..."
    override val noEpisodesAvailable = "تعذر جلب قائمة الحلقات لهذا المسلسل."
    override val seasonSuffix = "الموسم"
    override val durationPrefix = "المدة"
    override val favoriteShort = "مفضل"

    override val aiTitle = "دليل JHE HAKAR الذكي"
    override val aiCatalogSummaryPrefix = "ملخص المكتبة"
    override val aiSubtitle = "اختر سؤالاً سريعاً بجهاز التحكم أو اكتب ما ترغب بمشاهدته."
    override val aiQuickPrompts = listOf(
        "ماذا تنصحني أن أشاهد الليلة من مكتبتي؟",
        "اقترح لي أفضل أفلام الأكشن والإثارة",
        "كيف أنظم فئات البث المباشر والأفلام بشكل أفضل؟",
        "اقترح مسلسلات وأفلام عائلية ممتعة"
    )
    override val aiInputPlaceholder = "مثال: اقترح فيلم خيال علمي مشوق..."
    override val aiSendBtn = "إرسال"
    override val aiIdleTitle = "جاهز للاقتراحات الذكية"
    override val aiIdleDesc = "اضغط على أحد الأسئلة السريعة أعلاه أو اكتب سؤالك."
    override val aiLoadingText = "يقوم Gemini بتحليل مكتبتك..."
    override val aiStreamingTitle = "جاري كتابة الرد..."
    override val aiSuccessTitle = "اقتراح Gemini"
    override val aiClearBtn = "مسح"
    override val aiQuestionPrefix = "السؤال"
    override val aiErrorTitle = "تعذر الحصول على رد AI"
    override val aiRetryBtn = "إعادة المحاولة"

    override val languageSectionTitle = "لغة التطبيق / Language"
    override val languageSectionSub = "التبديل الفوري بين التركية والعربية والكردية (سوراني / باديني)"
    override val xtreamConnectTitle = "ربط لوحة Xtream IPTV"
    override val xtreamEditTitle = "تعديل بيانات السيرفر"
    override val xtreamConnectSub = "الصق رابط Xtream مباشرة أو أدخل بيانات السيرفر يدوياً"
    override val smartPasteLabel = "لصق ذكي (رابط M3U / Xtream أو نص البيانات)"
    override val parseBtn = "استخراج"
    override val parseSuccessMsg = "تم ملء بيانات Xtream تلقائياً!"
    override val parseFailMsg = "تعذر استخراج الرابط، يمكنك إدخال الحقول يدوياً."
    override val panelNameLabel = "اسم السيرفر"
    override val serverUrlLabel = "عنوان السيرفر (URL:Port)"
    override val usernameLabel = "اسم المستخدم"
    override val passwordLabel = "كلمة المرور"
    override val connectAndSyncBtn = "اتصال ومزامنة"
    override val connectingBtn = "جاري الاتصال..."
    override val cancelBtn = "إلغاء"
    override val fillSanazBtn = "تعبئة بيانات SANAZ"
    override val sanazFilledMsg = "تم تعبئة بيانات سيرفر SANAZ في النموذج."
    override val savedPanelsTitle = "السيرفرات المحفوظة"
    override val savedPanelsSub = "بدّل بين السيرفرات بنقرة واحدة أو حدّث القنوات"
    override val syncNowBtn = "مزامنة الآن"
    override val selectBtn = "اختيار"
    override val statusPrefix = "الحالة"
    override val expiryPrefix = "الانتهاء"
    override val connectionsPrefix = "الاتصال"
    override val playerSettingsTitle = "إعدادات المشغل والبث"
    override val playerSettingsSub = "اختر صيغة البث المباشر (HLS .m3u8 موصى به لأغلب أجهزة TV Box)"
    override val clearHistoryBtn = "مسح السجل"
    override val categoryOrganizerTitle = "منظم الفئات والمحتوى"
    override val categoryOrganizerSub = "أخفِ الفئات غير المرغوبة وثبّت الفئات المفضلة في الأعلى"
    override val showAllBtn = "إظهار الكل"
    override val hideAllBtn = "إخفاء الكل"
    override val filterCategoryHint = "تصفية اسم الفئة..."
    override val visibleLabel = "ظاهر"
    override val hiddenLabel = "مخفي"
    override val pinnedLabel = "مثبت"

    override val resizeFit = "ملاءمة"
    override val resizeZoom = "تكبير"
    override val resizeFill = "ملء الشاشة"
    override val playerErrorTitle = "خطأ في البث"
    override val playerBackBtn = "رجوع"
    override val epgNowPrefix = "الآن"
    override val epgNextPrefix = "التالي"
    override val playerAudioBtn = "الصوت"
    override val playerAudioTracksTitle = "مسار الصوت وفك التشفير"
    override val playerAudioAutoOption = "تلقائي (موصى به)"
    override val playerAudioNoneLabel = "لم يتم اكتشاف مسار صوتي"
    override val playerAudioDecoderAuto = "الصوت: تلقائي"
    override val playerAudioDecoderFfmpeg = "الصوت: FFmpeg"

    override val parentalSectionTitle = "الرقابة الأبوية (PIN)"
    override val parentalSectionSub = "إخفاء قنوات أو فئات محددة وحمايتها برمز PIN"
    override val parentalStatusActiveLocked = "مفعل • مقفل"
    override val parentalStatusActiveUnlocked = "مفعل • مفتوح"
    override val parentalStatusDisabled = "معطل"
    override val parentalSetupPinBtn = "إنشاء PIN والتفعيل"
    override val parentalUnlockBtn = "فتح القفل برمز PIN"
    override val parentalLockBtn = "قفل الآن"
    override val parentalChangePinBtn = "تغيير PIN"
    override val parentalDisableBtn = "تعطيل"
    override val parentalEnableBtn = "تفعيل"
    override val parentalPinDialogCreateTitle = "إنشاء رمز الرقابة الأبوية"
    override val parentalPinDialogUnlockTitle = "أدخل رمز الرقابة الأبوية"
    override val parentalPinDialogChangeTitle = "تغيير رمز الرقابة الأبوية"
    override val parentalPinLabel = "رمز PIN (4-8 أرقام)"
    override val parentalPinConfirmLabel = "تأكيد الرمز"
    override val parentalPinInvalidError = "يجب أن يتكون الرمز من 4 إلى 8 أرقام."
    override val parentalPinMismatchError = "رمزا PIN غير متطابقين."
    override val parentalPinWrongError = "رمز PIN غير صحيح."
    override val parentalSaveBtn = "حفظ"
    override val parentalChannelHidingTitle = "إخفاء القنوات وإظهارها"
    override val parentalChannelHidingSub = "إخفاء قنوات البث المباشر المحددة أو إعادتها"
    override val parentalFilterAllChannels = "الكل"
    override val parentalFilterHiddenChannels = "المخفية"
    override val parentalUnhideAllBtn = "إظهار كل القنوات"
    override val parentalHideChannelAction = "إخفاء القناة"
    override val parentalShowChannelAction = "إظهار القناة"
    override val parentalLockedNotice = "افتح القفل برمز PIN لتعديل القنوات أو الفئات المخفية."
    override val parentalHiddenCountSuffix = "مخفي"

    override val youtubeTitle = "يوتيوب Smart TV"
    override val youtubeSubtitle = "وضع التلفاز بالريموت، بحث سريع، بث مباشر وقنوات مثبتة"
    override val youtubeSearchHint = "ابحث في يوتيوب أو الصق رابط الفيديو..."
    override val youtubeGoBtn = "بحث"
    override val youtubeModeTvBtn = "وضع TV"
    override val youtubeModeWebBtn = "وضع الويب"
    override val youtubeCatHome = "الرئيسية"
    override val youtubeCatLive = "مباشر"
    override val youtubeCatMusic = "موسيقى"
    override val youtubeCatNews = "أخبار"
    override val youtubeCatSports = "رياضة"
    override val youtubeBackBtn = "رجوع"
    override val youtubeReloadBtn = "تحديث"
    override val youtubeOpenAppBtn = "فتح بالتطبيق"
    override val youtubeSaveBookmarkBtn = "تثبيت"
    override val youtubeBookmarksTitle = "المثبتة"
    override val youtubeAppNotFoundMsg = "لم يتم العثور على تطبيق يوتيوب خارجي؛ يتم التشغيل داخلياً."

    override val themeSectionTitle = "مظهر التطبيق والثيمات"
    override val themeSectionSub = "اختر طابع الألوان المفضل لواجهة التلفاز الذكي"
    override val themeBtnLabel = "الثيم"
    override val themeActiveBadge = "نشط"
    override val themeNameCyber = "أوبسيديان سيبراني"
    override val themeNameAmethyst = "جمشت ملكي"
    override val themeNameEmerald = "استاد زمردي"
    override val themeNameCrimson = "سينما قرمزية"
    override val themeNameGolden = "أونيكس ذهبي"
    override val themeNameCobalt = "كوبالت المحيط"

    override val activationTitle = "مطلوب تفعيل التطبيق"
    override val activationSubtitle = "أدخل رمز التفعيل المكون من 4 أرقام لفتح JHE HAKAR على هذا الجهاز."
    override val activationCodeLabel = "رمز التفعيل"
    override val activationCodeHint = "أدخل الرمز المكون من 4 أرقام"
    override val activationSubmitBtn = "تفعيل والبدء"
    override val activationInvalidError = "رمز التفعيل غير صحيح. يرجى التحقق والمحاولة مرة أخرى."
    override val activationClearBtn = "مسح"
    override val activationOneTimeNote = "يُطلب هذا التفعيل لمرة واحدة فقط عند التثبيت الأول."

    override val kurdishFreeTitle = "القنوات الكردية المجانية"
    override val kurdishFreeSubtitle = "بث مباشر مجاني 100% للقنوات الكردية بدون اشتراك أو سيرفر"
    override val kurdishFreeBadge = "مجاني"
    override val kurdishFreeRefreshBtn = "تحديث"
    override val kurdishFreeLoading = "جاري تحميل القنوات الكردية المجانية..."
    override val kurdishFreeErrorTitle = "تعذر تحميل القنوات المجانية"
    override val kurdishFreeRetryBtn = "إعادة المحاولة"
    override val kurdishFreeEmptyTitle = "لا توجد قنوات"
    override val kurdishFreeEmptyDesc = "لا توجد قنوات كردية مجانية مطابقة للبحث أو اللهجة المحددة."
    override val kurdishDialectAll = "الكل"
    override val kurdishDialectZaxo = "زاخو / بادينان"
    override val kurdishDialectSorani = "سوراني"
    override val kurdishDialectKurmanji = "كرمانجي / باديني"
    override val kurdishDialectGeneral = "كردي عام"

    override val webPanelSectionTitle = "لوحة التحكم عبر الموقع والمتصفح"
    override val webPanelSectionSub = "أدر التطبيق من متصفح الهاتف أو الكمبيوتر أو اربطه بموقع إلكتروني خارجي"
    override val webPanelLocalTitle = "لوحة الويب المدمجة (نفس شبكة Wi-Fi)"
    override val webPanelLocalRunning = "نشط"
    override val webPanelLocalStopped = "متوقف"
    override val webPanelStartBtn = "تشغيل لوحة الويب"
    override val webPanelStopBtn = "إيقاف"
    override val webPanelCopyUrlBtn = "نسخ الرابط"
    override val webPanelOpenBrowserBtn = "فتح في المتصفح"
    override val webPanelUrlCopiedMsg = "تم نسخ رابط لوحة الويب."
    override val webPanelRemoteTitle = "لوحة موقع إلكتروني خارجي (JSON / M3U)"
    override val webPanelRemoteSub = "أدخل رابط موقعك لجلب القنوات وبيانات Xtream والإعلانات تلقائياً"
    override val webPanelRemoteUrlLabel = "رابط الموقع الإلكتروني (https://...)"
    override val webPanelConnectSyncBtn = "ربط ومزامنة الموقع"
    override val webPanelDisconnectBtn = "فصل الموقع"
    override val webPanelCopyTemplateBtn = "نسخ قالب JSON"
    override val webPanelTemplateCopiedMsg = "تم نسخ قالب JSON الخاص بلوحة الموقع."
    override val webPanelLastSyncPrefix = "آخر مزامنة"
}

private object SoraniStrings : AppStrings {
    override val navHome = "سەرەتا"
    override val navLive = "ڕاستەوخۆ"
    override val navKurdishFree = "کوردی TV"
    override val navMovies = "فیلمەکان"
    override val navSeries = "زنجیرەکان"
    override val navYoutube = "یوتیوب"
    override val navAi = "ڕێبەری AI"
    override val navManager = "بەڕێوەبردن"

    override val heroBadge = "خالد نجم سليفاني"
    override val panelStatusPrefix = "سێرڤەر"
    override val heroSubtitleConnected = "کەناڵە ڕاستەوخۆکان، فیلم و زنجیرەکان بە ڕێکخستنی هاوپۆل لە یەک شاشەدا."
    override val heroSubtitleEmpty = "بۆ دەستپێکردن زانیاری پانێڵی Xtream IPTV یان لینکی M3U زیاد بکە."
    override val btnAddXtreamPanel = "زیادکردنی پانێڵی Xtream"
    override val btnWatchLive = "سەیرکردنی ڕاستەوخۆ"
    override val btnRefreshCatalog = "نوێکردنەوەی لیست"
    override val btnSyncing = "نوێ دەکرێتەوە..."
    override val btnContentManager = "بەڕێوەبردنی ناوەڕۆک"
    override val emptyNoPanelTitle = "هێشتا هیچ پانێڵێکی Xtream گرێنەدراوە"
    override val emptyNoPanelDesc = "لە بەشی بەڕێوەبردنەوە ناونیشانی سێرڤەر، ناوی بەکارهێنەر و وشەی نهێنی بنووسە بۆ هاوکاتکردنی هەموو کەناڵ و فیلمەکان."
    override val emptyNoPanelCta = "گرێدانی کۆدی Xtream"
    override val statLiveChannels = "کەناڵی ڕاستەوخۆ"
    override val statMoviesVod = "فیلمەکان (VOD)"
    override val statSeries = "زنجیرەکان"
    override val statConnections = "پەیوەندییەکان"
    override val statExpiryDate = "بەرواری بەسەرچوون"
    override val unlimited = "بێ سنوور"
    override val unknown = "نەزانراو"
    override val recentlyWatchedChannels = "دوایین کەناڵە بینراوەکان"
    override val recentlyWatchedChannelsSub = "دوایین ٥ کەناڵی ڕاستەوخۆ کە لێتدایە"
    override val recentlyWatchedEmpty = "هێشتا هیچ کەناڵێک لێنەدراوە. دوایین ٥ کەناڵی ڕاستەوخۆ لێرە دەردەکەون."
    override val continueWatching = "بەردەوامبە لە سەیرکردن"
    override val continueWatchingSub = "دوایین کەناڵ، فیلم و ئەڵقە بینراوەکان"
    override val continueWatchingEmpty = "هێشتا هیچ ناوەڕۆکێک نەبینراوە. ئەو کەناڵ و فیلم و زنجیرانەی لێیان دەدەیت لێرە دەردەکەون."
    override val resumeFromPrefix = "بەردەوامبوون"
    override val removeFromHistoryDesc = "لابردن لە مێژوو"
    override val favoriteChannels = "کەناڵە دڵخوازەکان"
    override val favoriteChannelsSub = "دەستگەیشتنی خێرا بە یەک کرتە"
    override val allChannelsBtn = "هەموو کەناڵەکان"
    override val featuredMovies = "فیلمە هەڵبژێردراوەکان"
    override val featuredMoviesSub = "نوێترین فیلمەکانی ناو هاوپۆلەکانت"
    override val allMoviesBtn = "هەموو فیلمەکان"
    override val popularSeries = "زنجیرە بەناوبانگەکان"
    override val popularSeriesSub = "سەیرکردن بە شوێنکەوتنی وەرز و ئەڵقەکان"
    override val allSeriesBtn = "هەموو زنجیرەکان"
    override val seriesBadge = "زنجیرە"

    override val livePanelRequiredTitle = "پانێڵ پێویستە بۆ ڕاستەوخۆ"
    override val livePanelRequiredDesc = "بۆ پیشاندانی کەناڵە ڕاستەوخۆکان، پانێڵی Xtream لە بەشی بەڕێوەبردن زیاد بکە."
    override val addPanelBtn = "زیادکردنی پانێڵ"
    override val categoriesTitle = "هاوپۆلەکان"
    override val allChannelsTitle = "هەموو کەناڵەکان"
    override val searchChannelHint = "گەڕان بۆ کەناڵ..."
    override val favoritesLabel = "دڵخوازەکان"
    override val channelCountSuffix = "کەناڵ"
    override val noChannelFoundTitle = "هیچ کەناڵێک نەدۆزرایەوە"
    override val noChannelFoundDesc = "لەم هاوپۆلە یان گەڕانەدا هیچ کەناڵێکی ڕاستەوخۆ نییە."
    override val liveBadge = "ڕاستەوخۆ"

    override val moviesPanelRequiredTitle = "پانێڵ پێویستە بۆ فیلمەکان"
    override val moviesPanelRequiredDesc = "بۆ بینینی فیلمەکان، پانێڵی Xtream لە بەشی بەڕێوەبردن ببەستەوە."
    override val movieCategoriesTitle = "هاوپۆلی فیلمەکان"
    override val allMoviesTitle = "هەموو فیلمەکان"
    override val searchMovieHint = "گەڕان بۆ فیلم..."
    override val sortDefault = "بنەڕەتی"
    override val sortNameAsc = "بەپێی ناو (A-Z)"
    override val sortRatingDesc = "بەپێی هەڵسەنگاندن"
    override val sortRecentDesc = "نوێترینەکان"
    override val noMovieFoundTitle = "هیچ فیلمێک نەدۆزرایەوە"
    override val noMovieFoundDesc = "لەم هاوپۆلە یان گەڕانەدا فیلم بوونی نییە."
    override val loadingMovieDetails = "وردەکارییەکانی فیلم باردەکرێت..."
    override val noPlotAvailable = "کورتەی چیرۆکی ئەم فیلمە لەلایەن سێرڤەرەوە نەنێردراوە."
    override val directorLabel = "دەرهێنەر"
    override val castLabel = "ئەکتەرەکان"
    override val playMovieBtn = "لێدانی فیلم"
    override val inFavoritesBtn = "لە دڵخوازەکاندا"
    override val addToFavoritesBtn = "زیادکردن بۆ دڵخواز"
    override val closeBtn = "داخستن"

    override val seriesPanelRequiredTitle = "پانێڵ پێویستە بۆ زنجیرەکان"
    override val seriesPanelRequiredDesc = "بۆ بینینی زنجیرە و ئەڵقەکان، پانێڵی Xtream لە بەشی بەڕێوەبردن ببەستەوە."
    override val seriesCategoriesTitle = "هاوپۆلی زنجیرەکان"
    override val allSeriesTitle = "هەموو زنجیرەکان"
    override val searchSeriesHint = "گەڕان بۆ زنجیرە..."
    override val noSeriesFoundTitle = "هیچ زنجیرەیەک نەدۆزرایەوە"
    override val noSeriesFoundDesc = "لەم هاوپۆلە یان گەڕانەدا زنجیرە بوونی نییە."
    override val noSeriesDescAvailable = "زانیاری دەربارەی ئەم زنجیرەیە بەردەست نییە."
    override val loadingSeasons = "وەرز و ئەڵقەکان باردەکرێن..."
    override val noEpisodesAvailable = "لیستی ئەڵقەکان بۆ ئەم زنجیرەیە وەرنەگیرا."
    override val seasonSuffix = "وەرز"
    override val durationPrefix = "ماوە"
    override val favoriteShort = "دڵخواز"

    override val aiTitle = "ڕێبەری زیرەکی JHE HAKAR"
    override val aiCatalogSummaryPrefix = "کورتەی لیست"
    override val aiSubtitle = "بە کۆنتڕۆڵی تەلەڤزیۆن پرسیارێکی خێرا هەڵبژێرە یان ئەوەی دەتەوێت بینووسە."
    override val aiQuickPrompts = listOf(
        "ئەمشەو لە لیستەکەم چی سەیر بکەم؟",
        "باشترین فیلمەکانی ئەکشن و سەرکێشی پێشنیار بکە",
        "چۆن هاوپۆلی کەناڵ و فیلمەکانم ڕێکبخەم؟",
        "پێشنیاری فیلم و زنجیرەی خێزانی بکە"
    )
    override val aiInputPlaceholder = "نموونە: فیلمێکی خەیاڵی زانستی خۆشم پێشنیار بکە..."
    override val aiSendBtn = "پرسیار"
    override val aiIdleTitle = "ئامادەیە بۆ پێشنیاری زیرەک"
    override val aiIdleDesc = "کرتە لە یەکێک لە کارتی پرسیارەکانی سەرەوە بکە یان پرسیاری خۆت بنێرە."
    override val aiLoadingText = "Gemini لیستەکەت شیدەکاتەوە..."
    override val aiStreamingTitle = "وەڵام ئامادە دەکرێت..."
    override val aiSuccessTitle = "پێشنیاری Gemini"
    override val aiClearBtn = "سڕینەوە"
    override val aiQuestionPrefix = "پرسیار"
    override val aiErrorTitle = "وەڵامی AI وەرنەگیرا"
    override val aiRetryBtn = "هەوڵبدەرەوە"

    override val languageSectionTitle = "زمانی بەرنامە / Language"
    override val languageSectionSub = "گۆڕینی خێرا لە نێوان تورکی، عەرەبی و کوردی (سۆرانی / بادینی)"
    override val xtreamConnectTitle = "گرێدانی پانێڵی Xtream IPTV"
    override val xtreamEditTitle = "دەستکاری زانیاری پانێڵ"
    override val xtreamConnectSub = "لینکی Xtream ڕاستەوخۆ بلکێنە یان زانیارییەکانی سێرڤەر یەک بەیەک بنووسە"
    override val smartPasteLabel = "لکاندنی خێرا (لینکی M3U / Xtream یان دەقی زانیاری)"
    override val parseBtn = "شیکردنەوە"
    override val parseSuccessMsg = "زانیارییەکانی Xtream خۆکارانە پڕکرانەوە!"
    override val parseFailMsg = "لینکەکە نەناسرایەوە، دەتوانیت بە دەست بینووسیت."
    override val panelNameLabel = "ناوی پانێڵ"
    override val serverUrlLabel = "ناونیشانی سێرڤەر (URL:Port)"
    override val usernameLabel = "ناوی بەکارهێنەر"
    override val passwordLabel = "وشەی نهێنی"
    override val connectAndSyncBtn = "گرێدان و هاوکاتکردن"
    override val connectingBtn = "دەبەسترێتەوە..."
    override val cancelBtn = "پاشگەزبوونەوە"
    override val fillSanazBtn = "پڕکردنەوەی زانیاری SANAZ"
    override val sanazFilledMsg = "زانیارییەکانی پانێڵی SANAZ لە فۆرمەکەدا پڕکرانەوە."
    override val savedPanelsTitle = "پانێڵە تۆمارکراوەکانی Xtream"
    override val savedPanelsSub = "بە یەک کرتە لە نێوان پانێڵەکاندا بگۆڕە یان لیستەکە نوێ بکەرەوە"
    override val syncNowBtn = "هاوکاتکردن"
    override val selectBtn = "هەڵبژاردن"
    override val statusPrefix = "بارودۆخ"
    override val expiryPrefix = "کۆتایی"
    override val connectionsPrefix = "پەیوەندی"
    override val playerSettingsTitle = "ڕێکخستنی پەخشکەر و ستریم"
    override val playerSettingsSub = "جۆری پەخشی ڕاستەوخۆ هەڵبژێرە (HLS .m3u8 بۆ زۆربەی TV Box باشترە)"
    override val clearHistoryBtn = "سڕینەوەی مێژوو"
    override val categoryOrganizerTitle = "ڕێکخەری هاوپۆل و ناوەڕۆک"
    override val categoryOrganizerSub = "ئەو هاوپۆلانەی ناتەوێت بیانشارەوە و گرنگەکان لە سەرەوە جێگیر بکە"
    override val showAllBtn = "پیشاندانی هەموو"
    override val hideAllBtn = "شاردنەوەی هەموو"
    override val filterCategoryHint = "گەڕان لە ناوی هاوپۆل..."
    override val visibleLabel = "دیار"
    override val hiddenLabel = "شاراوە"
    override val pinnedLabel = "جێگیرکراو"

    override val resizeFit = "گونجاندن"
    override val resizeZoom = "گەورەکردن"
    override val resizeFill = "پڕی شاشە"
    override val playerErrorTitle = "هەڵەی پەخش"
    override val playerBackBtn = "گەڕانەوە"
    override val epgNowPrefix = "ئێستا"
    override val epgNextPrefix = "دواتر"
    override val playerAudioBtn = "دەنگ"
    override val playerAudioTracksTitle = "هەڵبژاردنی دەنگ و دیکۆدەر"
    override val playerAudioAutoOption = "خۆکار (پێشنیارکراو)"
    override val playerAudioNoneLabel = "هیچ دەنگێک لە پەخشەکەدا نییە"
    override val playerAudioDecoderAuto = "دەنگ: خۆکار"
    override val playerAudioDecoderFfmpeg = "دەنگ: FFmpeg"

    override val parentalSectionTitle = "کۆنتڕۆڵی دایک و باوک (PIN)"
    override val parentalSectionSub = "کەناڵ یان هاوپۆلە دیاریکراوەکان بە کۆدی PIN بشارەوە"
    override val parentalStatusActiveLocked = "چالاک • قفڵدراو"
    override val parentalStatusActiveUnlocked = "چالاک • کراوە"
    override val parentalStatusDisabled = "ناچالاک"
    override val parentalSetupPinBtn = "دانانی PIN و چالاککردن"
    override val parentalUnlockBtn = "کردنەوەی قفڵ بە PIN"
    override val parentalLockBtn = "قفڵکردن"
    override val parentalChangePinBtn = "گۆڕینی PIN"
    override val parentalDisableBtn = "ناچالاککردن"
    override val parentalEnableBtn = "چالاککردن"
    override val parentalPinDialogCreateTitle = "دروستکردنی کۆدی PIN"
    override val parentalPinDialogUnlockTitle = "کۆدی PIN بنووسە"
    override val parentalPinDialogChangeTitle = "گۆڕینی کۆدی PIN"
    override val parentalPinLabel = "کۆدی PIN (4-8 ژمارە)"
    override val parentalPinConfirmLabel = "دووبارەکردنەوەی PIN"
    override val parentalPinInvalidError = "کۆدی PIN دەبێت 4 تا 8 ژمارە بێت."
    override val parentalPinMismatchError = "کۆدەکانی PIN یەکسان نین."
    override val parentalPinWrongError = "کۆدی PIN هەڵەیە."
    override val parentalSaveBtn = "پاشەکەوت"
    override val parentalChannelHidingTitle = "شاردنەوەی کەناڵەکان"
    override val parentalChannelHidingSub = "کەناڵە ڕاستەوخۆ دیاریکراوەکان بشارەوە یان پیشان بدەرەوە"
    override val parentalFilterAllChannels = "هەموو"
    override val parentalFilterHiddenChannels = "شاراوەکان"
    override val parentalUnhideAllBtn = "پیشاندانی هەموو کەناڵەکان"
    override val parentalHideChannelAction = "شاردنەوەی کەناڵ"
    override val parentalShowChannelAction = "پیشاندانی کەناڵ"
    override val parentalLockedNotice = "بۆ دەستکاریکردنی کەناڵ و هاوپۆلە شاراوەکان قفڵ بە PIN بکەرەوە."
    override val parentalHiddenCountSuffix = "شاراوە"

    override val youtubeTitle = "یوتیوب Smart TV"
    override val youtubeSubtitle = "دۆخی کۆنتڕۆڵی TV، گەڕانی خێرا، پەخشی ڕاستەوخۆ و کەناڵە جێگیرکراوەکان"
    override val youtubeSearchHint = "لە یوتیوب بگەڕێ یان لینکی ڤیدیۆ بلکێنە..."
    override val youtubeGoBtn = "گەڕان"
    override val youtubeModeTvBtn = "دۆخی TV"
    override val youtubeModeWebBtn = "دۆخی وێب"
    override val youtubeCatHome = "سەرەتا"
    override val youtubeCatLive = "ڕاستەوخۆ"
    override val youtubeCatMusic = "مۆسیقا"
    override val youtubeCatNews = "هەواڵ"
    override val youtubeCatSports = "وەرزش"
    override val youtubeBackBtn = "گەڕانەوە"
    override val youtubeReloadBtn = "نوێکردنەوە"
    override val youtubeOpenAppBtn = "لە ئەپدا بکەرەوە"
    override val youtubeSaveBookmarkBtn = "جێگیرکردن"
    override val youtubeBookmarksTitle = "جێگیرکراوەکان"
    override val youtubeAppNotFoundMsg = "ئەپی دەرەکی یوتیوب نەدۆزرایەوە؛ لە ناو بەرنامەدا بەردەوام دەبێت."

    override val themeSectionTitle = "ڕووکار و تێمی بەرنامە"
    override val themeSectionSub = "جۆری ڕەنگ و کەشوهەوای شاشەی تەلەڤزیۆن هەڵبژێرە"
    override val themeBtnLabel = "تێم"
    override val themeActiveBadge = "چالاک"
    override val themeNameCyber = "سایبەر ئۆبسیدیان"
    override val themeNameAmethyst = "ئەمەتیستی شاهانە"
    override val themeNameEmerald = "یاریگای زمورود"
    override val themeNameCrimson = "سینەمای سوور"
    override val themeNameGolden = "ئۆنیکسی زێڕین"
    override val themeNameCobalt = "کۆباڵتی زەریا"

    override val activationTitle = "چالاککردنی بەرنامە پێویستە"
    override val activationSubtitle = "کۆدی ٤ ژمارەیی چالاککردن بنووسە بۆ کردنەوەی JHE HAKAR لەم ئامێرەدا."
    override val activationCodeLabel = "کۆدی چالاککردن"
    override val activationCodeHint = "کۆدی ٤ ژمارەیی بنووسە"
    override val activationSubmitBtn = "چالاککردن و دەستپێکردن"
    override val activationInvalidError = "کۆدی چالاککردن هەڵەیە. تکایە دڵنیابە و دووبارە هەوڵبدە."
    override val activationClearBtn = "سڕینەوە"
    override val activationOneTimeNote = "ئەم کۆدە تەنها یەک جار لە یەکەم دامەزراندندا داوا دەکرێت."

    override val kurdishFreeTitle = "کەناڵە کوردییە خۆڕاییەکان"
    override val kurdishFreeSubtitle = "پەخشی ڕاستەوخۆی کەناڵە کوردییەکان بە خۆڕایی ١٠٠٪ بێ پێویستی بە بەشداربوون"
    override val kurdishFreeBadge = "خۆڕایی"
    override val kurdishFreeRefreshBtn = "نوێکردنەوە"
    override val kurdishFreeLoading = "کەناڵە کوردییە خۆڕاییەکان باردەکرێن..."
    override val kurdishFreeErrorTitle = "کەناڵە خۆڕاییەکان بارنەکران"
    override val kurdishFreeRetryBtn = "هەوڵبدەرەوە"
    override val kurdishFreeEmptyTitle = "هیچ کەناڵێک نەدۆزرایەوە"
    override val kurdishFreeEmptyDesc = "هیچ کەناڵێکی کوردی خۆڕایی لەگەڵ گەڕان یان زاراوەکەت نەگونجا."
    override val kurdishDialectAll = "هەموو"
    override val kurdishDialectZaxo = "زاخۆ / بادینی"
    override val kurdishDialectSorani = "سۆرانی"
    override val kurdishDialectKurmanji = "کورمانجی / بادینی"
    override val kurdishDialectGeneral = "کوردی گشتی"

    override val webPanelSectionTitle = "پانێڵی وێب و ماڵپەڕ"
    override val webPanelSectionSub = "بەرنامەکە لە وێبگەڕی مۆبایل/کۆمپیوتەرەوە بەڕێوەببە یان بەیەک ماڵپەڕەوە ببەستەرەوە"
    override val webPanelLocalTitle = "پانێڵی وێبی ناوخۆیی (هەمان Wi-Fi)"
    override val webPanelLocalRunning = "چالاکە"
    override val webPanelLocalStopped = "وەستاوە"
    override val webPanelStartBtn = "دەستپێکردنی پانێڵی وێب"
    override val webPanelStopBtn = "وەستاندن"
    override val webPanelCopyUrlBtn = "کۆپیکردنی لینک"
    override val webPanelOpenBrowserBtn = "کردنەوە لە وێبگەڕدا"
    override val webPanelUrlCopiedMsg = "لینکی پانێڵی وێب کۆپی کرا."
    override val webPanelRemoteTitle = "پانێڵی ماڵپەڕی دەرەکی (JSON / M3U)"
    override val webPanelRemoteSub = "ناونیشانی ماڵپەڕەکەت بنووسە بۆ هێنانی کەناڵ و سێرڤەرەکان"
    override val webPanelRemoteUrlLabel = "ناونیشانی ماڵپەڕ (https://...)"
    override val webPanelConnectSyncBtn = "گرێدان و هاوکاتکردن"
    override val webPanelDisconnectBtn = "پچڕاندنی گرێدان"
    override val webPanelCopyTemplateBtn = "کۆپیکردنی قاڵبی JSON"
    override val webPanelTemplateCopiedMsg = "قاڵبی JSON بۆ ماڵپەڕ کۆپی کرا."
    override val webPanelLastSyncPrefix = "دوایین هاوکاتکردن"
}

private object BadiniStrings : AppStrings {
    override val navHome = "سەرەکی"
    override val navLive = "ڕاستەوخۆ"
    override val navKurdishFree = "کوردی TV"
    override val navMovies = "فلم"
    override val navSeries = "زنجیرە"
    override val navYoutube = "یوتیوب"
    override val navAi = "ڕێنیشاندەرا AI"
    override val navManager = "ڕێڤەبەری"

    override val heroBadge = "خالد نجم سليفاني"
    override val panelStatusPrefix = "سێرڤەر"
    override val heroSubtitleConnected = "کەنالێن ڕاستەوخۆ، فلم و زنجیرە ب ڕێکوپێکی د یەک شاشێ دا."
    override val heroSubtitleEmpty = "بۆ دەستپێکرنێ زانیاریێن پانێلا Xtream IPTV یان لینکێ M3U زێدە بکە."
    override val btnAddXtreamPanel = "پانێلا Xtream زێدە بکە"
    override val btnWatchLive = "بەرێخۆدانا ڕاستەوخۆ"
    override val btnRefreshCatalog = "لیستێ نوو بکە"
    override val btnSyncing = "دهێتە نووکرن..."
    override val btnContentManager = "ڕێڤەبرنا ناڤەرۆکێ"
    override val emptyNoPanelTitle = "هێشتا چ پانێلا Xtream نەهاتیە گرێدان"
    override val emptyNoPanelDesc = "ژ پشکا ڕێڤەبەریێ ناڤونیشانێ سێرڤەری، ناڤێ بکارهێنەری و ژمارا نهێنی بنڤیسە دا هەمی کەنال و فلم بهێنە هەڤگرتن."
    override val emptyNoPanelCta = "کۆدێ Xtream گرێبدە"
    override val statLiveChannels = "کەنالێن ڕاستەوخۆ"
    override val statMoviesVod = "فلم (VOD)"
    override val statSeries = "زنجیرە"
    override val statConnections = "گرێدان"
    override val statExpiryDate = "دەمێ بدوماهی هاتنێ"
    override val unlimited = "بێ سنوور"
    override val unknown = "نەدیار"
    override val recentlyWatchedChannels = "دوماهی کەنالێن هاتینە دیتن"
    override val recentlyWatchedChannelsSub = "دوماهی ٥ کەنالێن ڕاستەوخۆ یێن تە لێداین"
    override val recentlyWatchedEmpty = "هێشتا چ کەنال نەهاتینە لێدان. دوماهی ٥ کەنالێن ڕاستەوخۆ دێ ل ڤێرە دیار بن."
    override val continueWatching = "بەردەوامبە ل سەر بەرێخۆدانێ"
    override val continueWatchingSub = "دوماهی کەنال، فلم و خەلەکێن تە سەحکرینێ"
    override val continueWatchingEmpty = "هێشتا چ ناڤەرۆک نەهاتینە دیتن. کەنال، فلم و زنجیرێن تە لێداین دێ ل ڤێرە هێنە نیشادان."
    override val resumeFromPrefix = "بەردەوامبە"
    override val removeFromHistoryDesc = "ژ مێژوویێ ڕاکە"
    override val favoriteChannels = "کەنالێن بژارە"
    override val favoriteChannelsSub = "دەستپێگەهشتنا بلەز ب ئێک کلیک"
    override val allChannelsBtn = "هەمی کەنال"
    override val featuredMovies = "فلمێن هەلبژارتی"
    override val featuredMoviesSub = "نووترین فلمێن ناڤا پشکێن تە"
    override val allMoviesBtn = "هەمی فلم"
    override val popularSeries = "زنجیرێن ناڤدار"
    override val popularSeriesSub = "بەرێخۆدان دگەل دیفچوونا وەرز و خەلەکان"
    override val allSeriesBtn = "هەمی زنجیرە"
    override val seriesBadge = "زنجیرە"

    override val livePanelRequiredTitle = "پانێل پێدڤیە بۆ ڕاستەوخۆ"
    override val livePanelRequiredDesc = "بۆ نیشادانا کەنالێن ڕاستەوخۆ، ژ پشکا ڕێڤەبەریێ پانێلا Xtream زێدە بکە."
    override val addPanelBtn = "پانێل زێدە بکە"
    override val categoriesTitle = "پشک (کەتەگۆری)"
    override val allChannelsTitle = "هەمی کەنال"
    override val searchChannelHint = "ل کەنالەکێ بگەڕە..."
    override val favoritesLabel = "بژارە"
    override val channelCountSuffix = "کەنال"
    override val noChannelFoundTitle = "چ کەنال نەهاتنە دیتن"
    override val noChannelFoundDesc = "د ڤێ پشکێ یان گەڕیانێ دا چ کەنالێن ڕاستەوخۆ نینن."
    override val liveBadge = "ڕاستەوخۆ"

    override val moviesPanelRequiredTitle = "پانێل پێدڤیە بۆ فلمان"
    override val moviesPanelRequiredDesc = "بۆ سەحکرنا فلمان، ژ پشکا ڕێڤەبەریێ پانێلا Xtream گرێبدە."
    override val movieCategoriesTitle = "پشکێن فلمان"
    override val allMoviesTitle = "هەمی فلم"
    override val searchMovieHint = "ل فلمەکێ بگەڕە..."
    override val sortDefault = "بنەرەتی"
    override val sortNameAsc = "ل دویڤ ناڤی (A-Z)"
    override val sortRatingDesc = "ل دویڤ هەلسەنگاندنێ"
    override val sortRecentDesc = "نووترین"
    override val noMovieFoundTitle = "چ فلم نەهاتنە دیتن"
    override val noMovieFoundDesc = "د ڤێ پشکێ یان گەڕیانێ دا چ فلم نینن."
    override val loadingMovieDetails = "زانیاریێن فلمی دهێنە بارکرن..."
    override val noPlotAvailable = "کورتەیا ڤی فلمی ژ لایێ سێرڤەری ڤە نەهاتیە هنارتن."
    override val directorLabel = "دەرهێنەر"
    override val castLabel = "ئەکتەر"
    override val playMovieBtn = "فلمی لێبدە"
    override val inFavoritesBtn = "د بژارەیان دا"
    override val addToFavoritesBtn = "زێدە بکە بۆ بژارە"
    override val closeBtn = "بگرە"

    override val seriesPanelRequiredTitle = "پانێل پێدڤیە بۆ زنجیران"
    override val seriesPanelRequiredDesc = "بۆ سەحکرنا زنجیرە و خەلەکان، ژ پشکا ڕێڤەبەریێ پانێلا Xtream گرێبدە."
    override val seriesCategoriesTitle = "پشکێن زنجیران"
    override val allSeriesTitle = "هەمی زنجیرە"
    override val searchSeriesHint = "ل زنجیرەکێ بگەڕە..."
    override val noSeriesFoundTitle = "چ زنجیرە نەهاتنە دیتن"
    override val noSeriesFoundDesc = "د ڤێ پشکێ یان گەڕیانێ دا چ زنجیرە نینن."
    override val noSeriesDescAvailable = "زانیاری دەربارەی ڤێ زنجیرێ بەردەست نینن."
    override val loadingSeasons = "وەرز و خەلەک دهێنە بارکرن..."
    override val noEpisodesAvailable = "لیستا خەلەکان بۆ ڤێ زنجیرێ نەهاتە وەرگرتن."
    override val seasonSuffix = "وەرز"
    override val durationPrefix = "ماوە"
    override val favoriteShort = "بژارە"

    override val aiTitle = "ڕێنیشاندەرا زیرەک یا JHE HAKAR"
    override val aiCatalogSummaryPrefix = "کورتەیا لیستێ"
    override val aiSubtitle = "ب کۆنترۆلا تەلەڤزیۆنێ پرسیارەکا بلەز هەلبژێرە یان ئەو تشتێ تە دڤێت بنڤیسە."
    override val aiQuickPrompts = listOf(
        "ئەڤ شەڤە ژ لیستا خۆ ل چ سەحکەم؟",
        "باشترین فلمێن ئەکشن و سەرکێشی پێشنیار بکە",
        "چەوا پشکێن کەنال و فلمێن خۆ ڕێکبێخم؟",
        "پێشنیارا فلم و زنجیرێن خێزانی بکە"
    )
    override val aiInputPlaceholder = "نموونە: فلمەکێ خەیالا زانستی یێ خوش پێشنیار بکە..."
    override val aiSendBtn = "بپرسە"
    override val aiIdleTitle = "ئامادەیە بۆ پێشنیارێن زیرەک"
    override val aiIdleDesc = "کلیک ل ئێک ژ کارتێن سەری بکە یان پرسیارا خۆ بهنێرە."
    override val aiLoadingText = "Gemini لیستا تە شڕۆڤە دکەت..."
    override val aiStreamingTitle = "بەرسڤ دهێتە ئامادەکرن..."
    override val aiSuccessTitle = "پێشنیارا Gemini"
    override val aiClearBtn = "پاقژ بکە"
    override val aiQuestionPrefix = "پرسیار"
    override val aiErrorTitle = "بەرسڤا AI نەهاتە وەرگرتن"
    override val aiRetryBtn = "دیسا هەولدە"

    override val languageSectionTitle = "زمانێ بەرنامەی / Language"
    override val languageSectionSub = "گۆهۆڕینا بلەز د ناڤبەرا تورکی، عەرەبی و کوردی (سۆرانی / بادینی) دا"
    override val xtreamConnectTitle = "گرێدانا پانێلا Xtream IPTV"
    override val xtreamEditTitle = "گوهارتنا زانیاریێن پانێلێ"
    override val xtreamConnectSub = "لینکێ Xtream ڕاستەوخۆ بلکینە یان زانیاریێن سێرڤەری ئێک ب ئێک بنڤیسە"
    override val smartPasteLabel = "لکاندنا بلەز (لینکێ M3U / Xtream یان دەقێ زانیاریان)"
    override val parseBtn = "جودا بکە"
    override val parseSuccessMsg = "زانیاریێن Xtream خۆکارانە هاتنە پڕکرن!"
    override val parseFailMsg = "لینک نەهاتە ناسین، دکاری ب دەست بنڤیسی."
    override val panelNameLabel = "ناڤێ پانێلێ"
    override val serverUrlLabel = "ناڤونیشانێ سێرڤەری (URL:Port)"
    override val usernameLabel = "ناڤێ بکارهێنەری"
    override val passwordLabel = "پەیڤا نهێنی"
    override val connectAndSyncBtn = "گرێبدە و نوو بکە"
    override val connectingBtn = "دهێتە گرێدان..."
    override val cancelBtn = "پاشگەزبوون"
    override val fillSanazBtn = "زانیاریێن SANAZ پڕ بکە"
    override val sanazFilledMsg = "زانیاریێن پانێلا SANAZ د فۆرمێ دا هاتنە پڕکرن."
    override val savedPanelsTitle = "پانێلێن تۆمارکری یێن Xtream"
    override val savedPanelsSub = "ب ئێک کلیک د ناڤبەرا پانێلان دا بگوهێزە یان لیستێ نوو بکە"
    override val syncNowBtn = "نوو بکە"
    override val selectBtn = "هەلبژێرە"
    override val statusPrefix = "ڕەوش"
    override val expiryPrefix = "دوماهی"
    override val connectionsPrefix = "گرێدان"
    override val playerSettingsTitle = "ڕێکخستنێن پەخشکەری و ستریمی"
    override val playerSettingsSub = "جۆرێ پەخشا ڕاستەوخۆ هەلبژێرە (HLS .m3u8 بۆ پتریا TV Box باشترە)"
    override val clearHistoryBtn = "مێژوویێ پاقژ بکە"
    override val categoryOrganizerTitle = "ڕێکخەرا پشک و ناڤەرۆکێ"
    override val categoryOrganizerSub = "پشکێن نەپێدڤی بڤەشێرە و یێن گرنگ ل سەری جێگیر بکە"
    override val showAllBtn = "هەمیان نیشان بدە"
    override val hideAllBtn = "هەمیان بڤەشێرە"
    override val filterCategoryHint = "ل ناڤێ پشکێ بگەڕە..."
    override val visibleLabel = "دیار"
    override val hiddenLabel = "ڤەشارتی"
    override val pinnedLabel = "جێگیرکری"

    override val resizeFit = "گونجاندن"
    override val resizeZoom = "نێزیککرن"
    override val resizeFill = "تژی شاشە"
    override val playerErrorTitle = "شاشیا پەخشێ"
    override val playerBackBtn = "ڤەگەڕە"
    override val epgNowPrefix = "نوکە"
    override val epgNextPrefix = "پاشی"
    override val playerAudioBtn = "دەنگ"
    override val playerAudioTracksTitle = "هەلبژارتنا دەنگی و دیکۆدەر"
    override val playerAudioAutoOption = "خۆکار (پێشنیارکری)"
    override val playerAudioNoneLabel = "چ دەنگ د پەخشێ دا نینن"
    override val playerAudioDecoderAuto = "دەنگ: خۆکار"
    override val playerAudioDecoderFfmpeg = "دەنگ: FFmpeg"

    override val parentalSectionTitle = "کۆنترۆلا دایک و بابان (PIN)"
    override val parentalSectionSub = "کەنال یان پشکێن دیارکری ب کۆدێ PIN بڤەشێرە"
    override val parentalStatusActiveLocked = "چالاک • قفلکری"
    override val parentalStatusActiveUnlocked = "چالاک • ڤەکری"
    override val parentalStatusDisabled = "نەچالاک"
    override val parentalSetupPinBtn = "دانانا PIN و چالاککرن"
    override val parentalUnlockBtn = "ڤەکرنا قفلێ ب PIN"
    override val parentalLockBtn = "قفلکرن"
    override val parentalChangePinBtn = "گوهارتنا PIN"
    override val parentalDisableBtn = "نەچالاککرن"
    override val parentalEnableBtn = "چالاککرن"
    override val parentalPinDialogCreateTitle = "دروستکرنا کۆدێ PIN"
    override val parentalPinDialogUnlockTitle = "کۆدێ PIN بنڤیسە"
    override val parentalPinDialogChangeTitle = "گوهارتنا کۆدێ PIN"
    override val parentalPinLabel = "کۆدێ PIN (4-8 ژمارە)"
    override val parentalPinConfirmLabel = "دوپاتکرنا PIN"
    override val parentalPinInvalidError = "دڤێت PIN ژ 4 هەتا 8 ژماران بیت."
    override val parentalPinMismatchError = "کۆدێن PIN وەکی ئێک نینن."
    override val parentalPinWrongError = "کۆدێ PIN شاشە."
    override val parentalSaveBtn = "پاراستن"
    override val parentalChannelHidingTitle = "ڤەشارتنا کەنالان"
    override val parentalChannelHidingSub = "کەنالێن ڕاستەوخۆ یێن دیارکری بڤەشێرە یان دیار بکە"
    override val parentalFilterAllChannels = "هەمی"
    override val parentalFilterHiddenChannels = "ڤەشارتی"
    override val parentalUnhideAllBtn = "هەمی کەنالان نیشان بدە"
    override val parentalHideChannelAction = "کەنالی بڤەشێرە"
    override val parentalShowChannelAction = "کەنالی نیشان بدە"
    override val parentalLockedNotice = "بۆ دەستکاریکرنا کەنال و پشکێن ڤەشارتی قفلێ ب PIN ڤەکە."
    override val parentalHiddenCountSuffix = "ڤەشارتی"

    override val youtubeTitle = "یوتیوب Smart TV"
    override val youtubeSubtitle = "شێوازێ کۆنترۆلا TV، گەڕیانا بلەز، پەخشا ڕاستەوخۆ و کەنالێن جێگیرکری"
    override val youtubeSearchHint = "ل یوتیوب بگەڕە یان لینکێ ڤیدیۆیێ بلکینە..."
    override val youtubeGoBtn = "گەڕیان"
    override val youtubeModeTvBtn = "شێوازێ TV"
    override val youtubeModeWebBtn = "شێوازێ وێب"
    override val youtubeCatHome = "سەرەکی"
    override val youtubeCatLive = "ڕاستەوخۆ"
    override val youtubeCatMusic = "موزیک"
    override val youtubeCatNews = "دەنگوباس"
    override val youtubeCatSports = "وەرزش"
    override val youtubeBackBtn = "ڤەگەڕە"
    override val youtubeReloadBtn = "نووکرن"
    override val youtubeOpenAppBtn = "د ئەپی دا ڤەکە"
    override val youtubeSaveBookmarkBtn = "جێگیر بکە"
    override val youtubeBookmarksTitle = "جێگیرکری"
    override val youtubeAppNotFoundMsg = "ئەپێ دەرەکی یێ یوتیوب نەهاتە دیتن؛ د ناڤ بەرنامەی دا دێ بەردەوام بیت."

    override val themeSectionTitle = "ڕووکار و تێمێ بەرنامەی"
    override val themeSectionSub = "ڕەنگ و شێوازێ گونجای بۆ شاشا تەلەڤزیۆنێ هەلبژێرە"
    override val themeBtnLabel = "تێم"
    override val themeActiveBadge = "چالاک"
    override val themeNameCyber = "سایبەر ئۆبسیدیان"
    override val themeNameAmethyst = "ئەمەتیستێ شاهانە"
    override val themeNameEmerald = "یاریگەها زمورود"
    override val themeNameCrimson = "سینەمایا سوور"
    override val themeNameGolden = "ئۆنیکسێ زێڕین"
    override val themeNameCobalt = "کۆباڵتێ زەریای"

    override val activationTitle = "چالاککرنا بەرنامەی پێدڤیە"
    override val activationSubtitle = "کۆدێ ٤ ژمارەیی یێ چالاککرنێ بنڤیسە دا JHE HAKAR ل سەر ڤی ئامێری ڤەبیت."
    override val activationCodeLabel = "کۆدێ چالاککرنێ"
    override val activationCodeHint = "کۆدێ ٤ ژمارەیی بنڤیسە"
    override val activationSubmitBtn = "چالاک بکە و دەستپێبکە"
    override val activationInvalidError = "کۆدێ چالاککرنێ شاشە. هیڤیە پشتراست بە و دیسا هەولدە."
    override val activationClearBtn = "پاقژ بکە"
    override val activationOneTimeNote = "ئەڤ پشتراستکرنە تنێ ئێک جار د دامەزراندنا ئێکێ دا دهێتە خواستن."

    override val kurdishFreeTitle = "کەنالێن کوردی یێن بێ بەرامبەر"
    override val kurdishFreeSubtitle = "پەخشا ڕاستەوخۆ یا کەنالێن کوردی ١٠٠٪ بێ بەرامبەر و بێ پێدڤیبوون ب بەشداربوونێ"
    override val kurdishFreeBadge = "بێ بەرامبەر"
    override val kurdishFreeRefreshBtn = "نووکرن"
    override val kurdishFreeLoading = "کەنالێن کوردی یێن بێ بەرامبەر دهێنە بارکرن..."
    override val kurdishFreeErrorTitle = "کەنالێن بێ بەرامبەر نەهاتنە بارکرن"
    override val kurdishFreeRetryBtn = "دیسا هەولدە"
    override val kurdishFreeEmptyTitle = "چ کەنال نەهاتنە دیتن"
    override val kurdishFreeEmptyDesc = "چ کەنالێن کوردی یێن بێ بەرامبەر دگەل گەڕیان یان زارڤەکرنا تە نەگونجان."
    override val kurdishDialectAll = "هەمی"
    override val kurdishDialectZaxo = "Zaxo / Badînî"
    override val kurdishDialectSorani = "سۆرانی"
    override val kurdishDialectKurmanji = "کورمانجی / بادینی"
    override val kurdishDialectGeneral = "کوردیێ گشتی"

    override val webPanelSectionTitle = "پانێلا وێب و ماڵپەڕی"
    override val webPanelSectionSub = "بەرنامەی ژ وێبگەڕێ مۆبایل/کۆمپیوتەری بڕێڤەببە یان ب ماڵپەڕەکێ دەرەکی ڤە گرێبدە"
    override val webPanelLocalTitle = "پانێلا وێب یا ناڤخۆیی (هەمان Wi-Fi)"
    override val webPanelLocalRunning = "کارا یە"
    override val webPanelLocalStopped = "ڕاوەستایە"
    override val webPanelStartBtn = "پانێلا وێب دەستپێبکە"
    override val webPanelStopBtn = "ڕاوەستینە"
    override val webPanelCopyUrlBtn = "لینکی کۆپی بکە"
    override val webPanelOpenBrowserBtn = "د وێبگەڕی دا ڤەکە"
    override val webPanelUrlCopiedMsg = "لینکێ پانێلا وێب هاتە کۆپیکرن."
    override val webPanelRemoteTitle = "پانێلا ماڵپەڕێ دەرەکی (JSON / M3U)"
    override val webPanelRemoteSub = "ناڤونیشانێ ماڵپەڕێ خۆ بنڤیسە دا کەنال و زانیاری بهێنە هەڤگرتن"
    override val webPanelRemoteUrlLabel = "ناڤونیشانێ ماڵپەڕی (https://...)"
    override val webPanelConnectSyncBtn = "گرێبدە و نوو بکە"
    override val webPanelDisconnectBtn = "گرێدانێ ببڕە"
    override val webPanelCopyTemplateBtn = "نمونەیا JSON کۆپی بکە"
    override val webPanelTemplateCopiedMsg = "نمونەیا JSON بۆ ماڵپەڕی هاتە کۆپیکرن."
    override val webPanelLastSyncPrefix = "دوماهی هەڤگرتن"
}
