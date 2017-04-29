# RESTAdapter for Android RecyclerView
### Extremely easy setup
Gradle
```gradle
compile 'com.suhel.restadapter:rest-adapter:1.0'
```
Maven
```xml
<dependency>
    <groupId>com.suhel.restadapter</groupId>
    <artifactId>rest-adapter</artifactId>
    <version>1.0</version>
    <type>pom</type>
</dependency>
```
Ivy
```xml
<dependency org='com.suhel.restadapter' name='rest-adapter' rev='1.0'>
  <artifact name='rest-adapter' ext='pom' ></artifact>
</dependency>
```
### Usage
##### Create a JSON in `res/raw` folder
###### `my_json.json`
```json
{
  "baseURL": "https://jsonplaceholder.typicode.com",
  "projections": {
    "posts": {
      "path": "/comments",
      "viewId": "item_card",
      "viewMap": {
        "email": "tvTitle",
        "body": "tvBody"
      },
      "dataTypeMap": {
        "email": "text",
        "body": "text"
      }
    }
  }
}
```
##### Create the adapter
###### MainActivity.java
```java
RESTAdapter adapter = new RESTAdapter.Builder()
                            .withContext(this)
                            .fromJSON(R.raw.my_json)
                            .loadProjection("posts")
                            .build();
RecyclerView list = (RecyclerView) findViewById(R.id.list);
list.setLayoutManager(new LinearLayoutManager(this));
list.setAdapter(adapter);
adapter.fetch();
```
