import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpEntity;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.FileOutputStream;
import java.io.IOException;

public class Main {

    //Наша ссылка, на которую будем отправлять запрос
   public static final String URI = "https://api.nasa.gov/planetary/apod?api_key=TuzY0sNtjHwGX0ShUGg5zT0VkZ8m9rW5gJywPVzf";

    //Сущность, которая будет преобразовывать ответ в наш объект NASA
    public static final ObjectMapper mapper = new ObjectMapper();



   public static void main (String[] args) throws IOException {
       //Настраиваем наш HTTP клиент, который будет отправлять запросы
       CloseableHttpClient httpClient = HttpClientBuilder.create()
               .setDefaultRequestConfig(RequestConfig.custom()
                       .setConnectTimeout(5000)    // максимальное время ожидание подключения к серверу
                       .setSocketTimeout(30000)    // максимальное время ожидания получения данных
                       .setRedirectsEnabled(false) // возможность следовать редиректу в ответе
                       .build())
               .build();

       //Отправляем запрос и получаем ответ
       CloseableHttpResponse response = httpClient.execute(new HttpGet(URI));

       //Преобразуем ответ в Java-объект NasaObject
       NasaObject nasaObject = mapper.readValue(response.getEntity().getContent(), NasaObject.class);
       System.out.println(nasaObject);

       // Отправляем запрос именно на картинку и получаем ответ с нашей картинкой
       CloseableHttpResponse pictureResponse = httpClient.execute(new HttpGet(nasaObject.getUrl()));

       //Формируем автоматически название для файла
       String[] arr = nasaObject.getUrl().split("/");
       String file = arr[arr.length - 1];

       //Проверяем что наш ответ не null
       HttpEntity entity = pictureResponse.getEntity();
       if (entity != null) {
           //сохраняем в файл
           FileOutputStream fos = new FileOutputStream(file);
           entity.writeTo(fos);
           fos.close();
       }





   }

}
