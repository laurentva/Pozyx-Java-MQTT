import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import structures.TagPosition;

import java.io.IOException;
import java.util.Random;

import com.google.gson.Gson;

public class MqttLocal implements MqttCallback {

    public static void main(String[] args) {
        new MqttLocal().run();
    }

    void run() {
        String topic = "tags";
        String broker = "tcp://localhost:1883";
        Random random = new Random();
        String clientId = String.format("JavaMQTTExample %d", random.nextInt(100000000));
        MemoryPersistence persistence = new MemoryPersistence();

        try {
            MqttClient client = new MqttClient(broker, clientId, persistence);

            MqttConnectOptions connectOptions = new MqttConnectOptions();
            connectOptions.setCleanSession(true);

            System.out.println("Connecting to broker: " + broker);
            client.connect(connectOptions);
            System.out.println("Connected");

            client.setCallback(this);
            client.subscribe(topic);
        } catch (MqttException mqttException) {
            System.out.println("Could not connect to local MQTT");
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
