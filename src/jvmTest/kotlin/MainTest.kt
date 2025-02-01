import aws.sdk.kotlin.runtime.auth.credentials.StaticCredentialsProvider
import aws.sdk.kotlin.services.bedrock.BedrockClient
import aws.sdk.kotlin.services.bedrockruntime.BedrockRuntimeClient
import aws.sdk.kotlin.services.bedrockruntime.invokeModel
import aws.smithy.kotlin.runtime.net.url.Url
import kotlinx.coroutines.runBlocking
import org.testcontainers.containers.GenericContainer
import org.testcontainers.containers.localstack.LocalStackContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.utility.DockerImageName
import kotlin.test.Test

@Testcontainers
class MainTest {

    companion object {
        private val localStackImage = DockerImageName.parse("localstack/localstack:4.1.0")

        @Container
        val localStack = LocalStackContainer(localStackImage)
    }

    // todo: actually requires localstack pro :(
    @Test
    fun test1() = runBlocking {
        val bedrockClient = BedrockClient.fromEnvironment {
            endpointUrl = Url.parse(localStack.endpoint.toString())
            region = localStack.region
            credentialsProvider = StaticCredentialsProvider {
                accessKeyId = localStack.accessKey
                secretAccessKey = localStack.secretKey
            }
        }

        val models = bedrockClient.listFoundationModels()
        println(models)

        /*
        val bedrockRuntimeClient = BedrockRuntimeClient.fromEnvironment()

        bedrockRuntimeClient.use { client ->

            val response = client.invokeModel {
                body = "say hello".encodeToRequest()
                modelId = ""
            }
            println(response.body?.asUtf8String())
        }

         */

        //println(bedrockRuntimeClient)

        assert(1 == 2)
    }

}
