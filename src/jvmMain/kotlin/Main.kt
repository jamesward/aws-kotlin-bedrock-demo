import aws.sdk.kotlin.services.bedrockruntime.BedrockRuntimeClient
import aws.sdk.kotlin.services.bedrockruntime.invokeModel
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonNamingStrategy

suspend fun main() {
    println(askBedrock("What is great about Kotlin 2.0?").content.first().text)
}


@Serializable
data class Content(val text: String, val type: String = "text")

@Serializable
data class Message(val role: String = "user", val content: List<Content>)

@Serializable
data class InvokeRequestBody(val anthropicVersion: String = "bedrock-2023-05-31", val maxTokens: Int = 4096, val messages: List<Message>)


@OptIn(ExperimentalSerializationApi::class)
private val json = Json {
    namingStrategy = JsonNamingStrategy.SnakeCase
    encodeDefaults = true
    ignoreUnknownKeys = true
}


fun String.encodeToRequest(): ByteArray =
    json.encodeToString(
        InvokeRequestBody(
            messages = listOf(
                Message(
                    content = listOf(
                        Content(text = this)
                    )
                )
            )
        )
    ).toByteArray()

fun ByteArray.decodeFromResponse(): Message =
    json.decodeFromString(this.decodeToString())

suspend fun askBedrock(s: String): Message =
    BedrockRuntimeClient { region = "us-west-2" }.use {
        it.invokeModel {
            modelId = "anthropic.claude-3-haiku-20240307-v1:0"
            body = s.encodeToRequest()
            contentType = "application/json"
        }.body.decodeFromResponse()
    }
