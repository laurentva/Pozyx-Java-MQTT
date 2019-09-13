# Pozyx-Java-MQTT

Java code to quickstart your Pozyx MQTT integration/application!

## Setup

### Prerequisites

To run this project, you will need:

* Pozyx Creator:
	- Pozyx Creator software installed.
	- Running setup with tags being positioned.
* OR Pozyx Enterprise:
	- Pozyx gateway with running tags.
	- The local IP of your gateway
* Java and Maven. The project was developed targeting Java bytecode 1.8 using 4.0.0.

### Installation

Clone the project using `git clone https://github.com/laurentva/Pozyx-Java-MQTT` and open the project in your favorite Java editor, like IntelliJ or Eclipse. 

## Running the project

You can find the main function in `src/main/java/Main.java`. The local MQTT will be enabled by default and the cloud MQTT will be commented out.

```java
public class Main {

    public static void main(String[] args) {
        runLocalMqtt();
//        runCloudMqtt();
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
```

If you have a local Creator setup running, you should be able to see positioning output right away when pressing play.

### Local Enterprise MQTT

To subscribe to the MQTT datastream, you'll have to change the IP on line 18 in `src/main/java/MqttLocal.java`.
You can find the IP address of your gateway on [this page](https://app.pozyx.io/settings/connectivity/local).
For example, when the IP is mqtt://192.168.67.118, you can change the code like this.

```java
String broker = "tcp://localhost:1883";
// TO        
String broker = "tcp://192.168.67.118:1883";
```

### Cloud MQTT

To subscribe to the MQTT datastream, you'll have to change the topic and apiKey on lines 17-18 in `src/main/java/MqttCloud.java`.
You can find this data on [this page](https://app.pozyx.io/settings/connectivity/cloud). If you do not have an API key yet, you can create one on this page as well.

```java
String topic = ""; // retrieve from https://app.pozyx.io/settings/connectivity/cloud
String apiKey = ""; // retrieve from https://app.pozyx.io/settings/connectivity/cloud
```


## Inside the code

In this "behind the scenes" bit we'll get more in-depth regarding the code, in a more developer-focused segment.

The main file is already shown above, the interesting code is in `src/main/java/MqttLocal.java` and `src/main/java/MqttCloud.java`.
The code in these classes will connect to its respective MQTT stream, and then print the incoming Pozyx positioning data in "ID;X;Y;Z" format.

### Connecting to the MQTT client

For the MQTT functionality, we're using the [Eclipse Paho Java Client](https://www.eclipse.org/paho/clients/java/). This supports WebSockets and SSL/TLS, and thus supports the Cloud MQTT.

You'll see that the MqttLocal implements the MqttCallback provided by the Paho library. This is useful as all callbacks will be contained by this class.
To successfully implement this class, we must provide overrides for `connectionLost`, `messageArrived` and `deliveryComplete`.

```java
public class MqttLocal implements MqttCallback {
    // ...
}
```

To connect to the local MQTT, we're using the TCP protocol and port 1883. A random client ID is provided to avoid conflicting IDs.
The connect options are simple, and for the Cloud MQTT we only add a username (the topic) and a password (the API key) to the connect options.

The callbacks on the MQTT stream, such as when the connection is lost or, most importantly, when a new message arrives, are bound to the class itself.

```java
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
            // ...
        }
    }
```


### Parsing the MQTT messages

We bound the callbacks on the MQTT stream to the class, and our main logic will happen in the `messageArrived` callback.

The tag data comes in as an array of individual tag packets. We'll be using the [Gson library](https://github.com/google/gson) provided by Google to decode these JSONs and make sense of the incoming data.
You can find all data container classes to parse the tag position in `src/main/java/structures`.

We check the tag position data on whether the positioning data was successfull, and if it was we print the ID, x, y, and z coordinates.

```java
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
```

## What's next?

You can always send issues, feedback, and requests for additional MQTT demos to support@pozyx.io.