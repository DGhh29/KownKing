# KownKing
Server-side code created based on SpringBoot + SpringAI, integrating Ollama and various AI model projects.

## Project startup environment
### **Ollama :**  

For the first time you start, you need to download the corresponding model from Ollama beforehand. However, you
can also auto-pull it during the first startup. But be warned that the first startup may take a long time and
might even timeout due to its slow performance.

Alternatively, you can configure a timeout limit for the first startup, allowing you to manage the wait time more
effectively.

<div style="text-align: center;">   <img src="https://ollama.com/public/ollama.png" alt="Example Image" width="200px"> </div>  

#### **Large language model :** [llama3.2](https://ollama.com/library/llama3.2)

#### **Embedded model : **[mxbai-embed-large](https://ollama.com/library/mxbai-embed-large)

![image-20241227005859088](https://dgimg.dghhfun.top//typora/24--12--27/d4e781eb-46da-4cc6-9731-c33ddce6f84c.png)

### **SpringAI**：[https://spring.io/projects/spring-ai](https://spring.io/projects/spring-ai)

<div style="text-align: center;">  <img src="https://images.ctfassets.net/mnrwi97vnhts/4mda205vy509Dx3vGkMwFr/af520e66dc79fb80cd1bc129a11d6d23/spring-ai-integration-diagram-3.svg" alt="Example Image" width="500px"></div> 

#### Vector Databases

``` java
@Bean
    public VectorStore vectorStore(JedisPooled jedisPooled, @Qualifier("ollamaEmbeddingModel")EmbeddingModel embeddingModel) {
        return RedisVectorStore.builder(jedisPooled,embeddingModel)
                .indexName("default-index")
                .prefix("default:")
                .initializeSchema(true)
                .batchingStrategy(new TokenCountBatchingStrategy())
                .build();
    }
```

You need to install RedisStack. You can use Docker to install it.

``` docker
docker run -d --name redis-stack --restart=always  -v redis-data:/data -p 6379:6379 -p 8001:8001 -e REDIS_ARGS="--requirepass 123456" redis/redis-stack:latest
```

#### FunctionCall  https://docs.spring.io/spring-ai/reference/api/functions.html

<div style="text-align: center;">  <img src="https://docs.spring.io/spring-ai/reference/_images/function-calling-basic-flow.jpg" alt="Example Image" width="500px"></div> 

``` java
@Description("Get the weather in location")
@Service
public class DocumentAnalyzerFunction implements Function<DocumentAnalyzerFunction.Request, DocumentAnalyzerFunction.Response> {
    /**
     * Make your own custom function or send an external API request.
     */
    @Autowired
    private WeatherUtils weatherUtils;
    
    @Data
    public static class Request {
        @JsonProperty(required = true, value = "city")
        @JsonPropertyDescription(value = "city name")
        String city;
    }

    public record Response(String result) {
    }

    @SneakyThrows
    @Override
    public Response apply(Request request) {

        Map<String,String> map = weatherUtils.cityRequest(request.city);
        String result = "";
        System.out.println(map);
        if (map!=null) result = "Weather conditions are"+map.get("weather")+"Temperature is"+map.get("temperature")+"°C";
        else result = "No accurate information found, please provide a correct city name.";
        return new Response(result);
    }


}
```

