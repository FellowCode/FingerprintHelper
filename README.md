# FingerprintHelper
Library for easy work with android fingerprint


Gradle: 
`compile 'com.github.fellowcode:FingerprintHelper:0.0.5'`

Class may be declared on API at least 14.

The class uses a set of methods for different fingerprint states:

```java
FingerprintMethods fingerprintMethods = new FingerprintMethods() {
	@Override
	public void onAuthenticationSucceeded() {
		//your code
	}

	@Override
	public void onAuthenticationError() {
		//your code
	}

	@Override
	public void onAuthenticationFailed() {
		//your code
	};
```

Method onAuthenticationError works after several onAuthenticationFailed consecutive.


The class is declared as follows:
```java
	FingerprintHelper fingerprintHelper = new FingerprintHelper(context, fingerprintMethods);
```

Return true if hardware and API support fingerprint:
```java
	fingerprintHelper.checkFinger();
```
All class methods can be used without `checkFinger()`.

To activate the sensor:
```java
	fingerprintHelper.prepareSensor();
```

To deactivate the sensor:
```java
	fingerprintHelper.stopSensor();
```

You can add ImageView to the state handler(must be declared before `prepareSensor()`) . `millis` - the time(milliseconds) through which the ImageView will return to the base state:
```java
	fingerprintHelper.setImage(millis, yourImageView, 
								<your_base_state_resource>,
								<your_succes_state_resource>,
								<your_fail_state_resource>);
```
For example:
```java
	fingerprintHelper.setImage(millis, yourImageView, 
								R.mipmap.ic_fingerprint_base,
								R.mipmap.ic_fingerprint_succes,
								R.mipmap.ic_fingerprint_fail);
```


