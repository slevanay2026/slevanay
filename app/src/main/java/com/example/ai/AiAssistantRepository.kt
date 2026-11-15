package com.example.ai

import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.content
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapNotNull

sealed interface AiUiState {
    data object Idle : AiUiState
    data object Loading : AiUiState
    data class Streaming(val text: String) : AiUiState
    data class Success(val text: String) : AiUiState
    data class Error(val message: String) : AiUiState
}

class AiAssistantRepository {
    private val model by lazy {
        Firebase.ai.generativeModel(
            modelName = "gemini-3.8-flash",
            systemInstruction = content {
                text(
                    "Sen JHE HAKAR (Android TV ve TV Box IPTV uygulaması) için akıllı izleme ve " +
                        "katalog asistanısın. Kullanıcılara canlı kanallar, filmler ve diziler hakkında " +
                        "Türkçe, net, kısa ve TV ekranında kolay okunabilir yanıtlar ver. " +
                        "Sana verilen katalog özetindeki başlıkları ve kategorileri kullanarak " +
                        "kişiselleştirilmiş öneriler sun."
                )
            }
        )
    }

    fun streamTvAdvice(prompt: String): Flow<String> {
        return model.generateContentStream(prompt).mapNotNull { chunk ->
            chunk.text
        }
    }
}
