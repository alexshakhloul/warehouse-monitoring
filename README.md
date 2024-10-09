# warehouse-monitoring

### System Design
Design a reactive system that includes:
- Warehouse Service: Collects data from various sensors and sends it to the Central
Monitoring Service.
- Central Monitoring Service: Configured with thresholds for temperature and
humidity. Raises an alarm when sensor measurements cross these thresholds. The
alarm message should be visible in the logs/console.


### Specifications
- Sensor Types: Temperature, Humidity
- Communication: Measurements are sent via UDP.
- Central Service Features: Threshold monitoring, alarm activation.
#### Technical Requirements:
- Temperature Sensor:
  - UDP Port: 3344
  - Measurement Syntax: sensor_id=t1; value=30 
  - Threshold: 35°C
- Humidity Sensor:
  - UDP Port: 3355
  - Measurement Syntax: sensor_id=h1; value=40 
  - Threshold: 50%

### Implementation
For demonstration purposes the system was designed in one project, splitting the central monitoring service and
warehouse service in different packages (as if they are completely different services) sharing a model 
(that also could be in a shared library)

[WarehouseService](com/wh/warehouse/service/WarehouseService.java) is the entry point, where it will 
create and start UDP listeners to get sensor data.
Transforming that data and publishing it to Kafka.

[CentralMonitoringService](com/wh/central/service/CentralMonitoringService.java) will consume the kafka topic and 
process the data checking if alarm needs to be raised or not.

Implementation was done using Java 17 and reactive programing based on Flux and Kafka as messaging broker between the warehouse
service and central monitoring.
Functional and some technical behaviour was covered by test cases.

### Executing the app
[docker-compose](docker-compose.yml) should be launched to run a kafka broker in a docker container so that our app could fully run.

[SimulatorApplication](com/wh/SimulatorApplication.java) was created to simulate indefinite random data being sent through UDP
and testing the app without manual or user interaction.

By starting [WarehouseMonitoringApplication](com/wh/WarehouseMonitoringApplication.java) our app will run seamlessly