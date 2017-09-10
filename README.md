# FingerprintHelper
Library for easy work with android fingerprint


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


Prior to declaring a class, an API check is required. The class is declared as follows:
```java
	FingerprintHelper fingerprintHelper = new FingerprintHelper(context, fingerprintMethods);
```

Check device and API fingerprint support:
```java
	fingerprintHelper.checkFinger();
```

To activate the sensor:
```java
	fingerprintHelper.prepareSensor();
```

To deactivate the sensor:
```java
	fingerprintHelper.stopSensor();
```

You can add ImageView to the state handler. `millis` - the time(milliseconds) through which the ImageView will return to the base state:
```java
	fingerprintHelper.setImage(millis, yourImageView, 
								<your_base_state_resource>,
								<your_succes_state_resource>,
								<your_fail_state_resurce>);
```
For example:
```java
	fingerprintHelper.setImage(millis, yourImageView, 
								R.mipmap.ic_fingerprint_base,
								R.mipmap.ic_fingerprint_succes,
								R.mipmap.ic_fingerprint_fail);
```


