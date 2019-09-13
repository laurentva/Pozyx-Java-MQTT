import com.google.gson.Gson;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import structures.TagPosition;

import java.io.IOException;
import java.util.Random;


public class MqttCloud implements MqttCallback {

    public static void main(String[] args) {
        new MqttCloud().run();
    }

    void run() {
        String topic = ""; // retrieve from https://app.pozyx.io/settings/connectivity/cloud
        String apiKey = ""; // retrieve from https://app.pozyx.io/settings/connectivity/cloud
        String broker = "wss://mqtt.cloud.pozyxlabs.com:443";

        Random random = new Random();
        String clientId = String.format("JavaMQTTExample %d", random.nextInt(100000000));
        MemoryPersistence persistence = new MemoryPersistence();

        try {
            MqttClient client = new MqttClient(broker, clientId, persistence);
            MqttConnectOptions connectOptions = new MqttConnectOptions();
            connectOptions.setCleanSession(true);
            connectOptions.setUserName(topic);
            connectOptions.setPassword(apiKey.toCharArray());
            System.out.println("Connecting to broker: " + broker);
            client.connect(connectOptions);
            System.out.println("Connected");

            client.setCallback(this);
            client.subscribe(topic);

        } catch (MqttException mqttException) {
            System.out.println("Could not connect to cloud MQTT");
            System.out.println("Reason code " + mqttException.getReasonCode());
            System.out.println("Message " + mqttException.getMessage());
            System.out.println("Localized message " + mqttException.getLocalizedMessage());
            System.out.println("Cause " + mqttException.getCause());
            System.out.println("Exception " + mqttException);
            mqttException.printStackTrace();
        }
    }

    @Override
    public void connectionLost(Throwable throwable) {

    }

    @Override
    public void messageArrived(String topic, MqttMessage message)
            throws Exception, IOException {
//        System.out.println(message);

        Gson gson = new Gson();
        TagPosition[] tagPositions = gson.fromJson(message.toString(), TagPosition[].class);

        for (TagPosition tagPosition : tagPositions) {
            if (tagPosition.success) {
                System.out.println(String.format("%s;%d;%d;%d", tagPosition.tagId, tagPosition.data.coordinates.x, tagPosition.data.coordinates.y, tagPosition.data.coordinates.z));
            }
        }
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

    }
}
