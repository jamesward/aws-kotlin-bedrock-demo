import aws.sdk.kotlin.services.bedrockruntime.BedrockRuntimeClient
import aws.sdk.kotlin.services.bedrockruntime.invokeModel
import aws.sdk.kotlin.services.s3.S3Client
import aws.sdk.kotlin.services.s3.createBucket
import aws.sdk.kotlin.services.s3.model.BucketLocationConstraint
import aws.sdk.kotlin.services.s3.model.PutObjectRequest
import aws.sdk.kotlin.services.s3.putObject
import aws.sdk.kotlin.services.s3.waiters.waitUntilBucketExists
import aws.smithy.kotlin.runtime.content.ByteStream
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonNamingStrategy
import java.util.UUID


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

val claude3Haiku = "anthropic.claude-3-haiku-20240307-v1:0"

suspend fun askBedrock(s: String): Message =
    BedrockRuntimeClient { region = "us-west-2" }.use {
        it.invokeModel {
            modelId = claude3Haiku
            body = s.encodeToRequest()
            contentType = "application/json"
        }.body.decodeFromResponse()
    }

suspend fun save(s: String): Unit =
    S3Client { region = "us-west-2" }.use { s3 ->
        val name = UUID.randomUUID().toString()
        s3.createBucket {
            bucket = name
            createBucketConfiguration {
                locationConstraint = BucketLocationConstraint.UsWest2
            }
        }
        s3.waitUntilBucketExists { bucket = name }
        s3.putObject {
            bucket = name
            key = "thing.txt"
            body = ByteStream.fromString(s)
        }
    }


suspend fun main() {
    val q = "What is great about Kotlin 2.0?"
    val a = askBedrock(q).content.first().text
    save(a)
}
