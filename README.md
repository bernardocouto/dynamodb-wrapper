# DynamoDB Wrapper

The objective of this project is to create an abstraction layer for use with DynamoDB implementing in the new version of Amazon AWS SDK.

## How to use

### Annotations

For mapping the entity the following annotations are necessary:

* @Table
* @PartitionKey
* @SortKey (Optional)

#### Sample

```java
@Table("user")
public class User {

    @PartitionKey(value = "user_id")
    private String id;

    @SortKey(value = "user_email")
    private String email;

    private String name;

    // Constructor

    // Getters and Setters

    // Others Methods

}
```

The annotations ```value``` field should be used optionally if the name of the keys in DynamoDB are different from the name of the variables created

### Methods

#### Criteria

* ```and``` - Adds a new Criteria to an existing chain of Criteria
* ```exists``` - Adds a condition using the ```attribute_exists(key)``` function of DynamoDB
* ```gt``` - Add a condition using the ```>``` operator
* ```gte``` - Add a condition using the ```>=``` operator
* ```is``` - Add a condition using the ```=``` operator
* ```lt``` - Add a condition using the ```<``` operator
* ```lte``` - Add a condition using the ```<=``` operator
* ```notExists``` - Adds a condition using the ```attribute_not_exists(key)``` function of DynamoDB
* ```partitionKey``` - Returns the PartitionKey alias
* ```sortKey``` - Returns the SortKey alias
* ```where``` - Add a new Criteria with the key entered

#### CriteriaImplementation

* ```from``` - Creates a new CriteriaImplementation with the informed Criteria

#### API

##### Find All

```java
List<User> users = operationImplementation.findAll(User.class);

List<User> users = operationImplementation.findAll(CriteriaImplementation.from(where(partitionKey()).is(id)), User.class);
```

##### Find One

```java
User user = operationImplementation.findOne(new Key(id), User.class());

User user = operationImplementation.findOne(new Key(id, email), User.class());

User user = operationImplementation.findOne(CriteriaImplementation.from(where(partitionKey()).is(id)), User.class);
```

##### Remove

```java
operationImplementation.remove(user);

operationImplementation.remove(new Key(id));

operationImplementation.remove(new Key(id, email));
```

##### Save

```java
operationImplementation.save(user);

operationImplementation.save(user, CriteriaImplementation.from(where(Criteria.sortKey()).is(email)));
```
