public class Main {

    public static void main(String[] args) {
//        runLocalMqtt();
        runCloudMqtt();
    }

    public static void runLocalMqtt() {
        MqttLocal mqttLocal = new MqttLocal();
        mqttLocal.run();
    }

    public static void runCloudMqtt() {
        MqttLocal mqttLocal = new MqttLocal();
        mqttLocal.run();
    }
}
