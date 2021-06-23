## Dynamicity of references

One of the additional things to consider when implementing a partial-evaluation friendly interpreter is dynamicity of references, where by dynamicity we mean how often the reference changes its state. This metric is important because effectively the dynamicity of a chain of references will be equal to the most dynamic of the references. As a result, what would usually be considered bad design patterns is sometimes necessary to divide the chain into more direct references, such that each object is reachable with the lowest dynamicity possible.

The following reference graph shows the refactoring in a generic case:

![alt](dynamicity_generic.drawio.svg)

_Scenario 1: Reference chain results in class B being accessible with high dynamicity and therefore not being effectively partially evaluated_

![alt](dynamicity_generic_good.drawio.svg)

_Scenario 2: Class B is accessible with a low dynamicity reference, resulting in more effective partial evaluation_

For a case study from the BACIL implementation, let's consider the design decisions behind `LocationDescriptor` and `LocationHolder`. Each location has a type and a value. While the value itself (and the type of the value) changes based on the running code, the type of the location never changes. This is a perfect example of two pieces of information with different dynamicity. 

Even from regular developement patterns, it makes sense to divide location values and location types into separate classes - store the location type information in the metadata as a "prototype" for then creating the value storage based on it. In BACIL, `LocationDescriptor` contains the Type information and `LocationHolder` contains the actual values.

To work with the values, it is always necessary to know the location type (mainly to differentiate between ValueTypes and references). The rule of encapsulation would dictate that the final caller doesn't need to know that there's a `LocationDescriptor` tied to the `LocationHolder`, as it's an internal detail. Such an implementation would look something like this:

```Java 
public class LocationHolder {
    private final LocationDescriptor descriptor;

    private final Object[] refs;
    private final long[] primitives;

    public LocationsHolder(LocationDescriptor descriptor) {
        this.descriptor = descriptor;
        refs = new Object[descriptor.getRefCount()];
        primitives = new long[descriptor.getPrimitiveCount()];
    }

    public Object locationToObject(int locationIndex)
    {
        return descriptor.locationToObject(this, locationIndex);
    }

}

//Accessing a field of an object
Object fieldValue = ((StaticObject)object).getLocationsHolder().locationToObject(0);
```

However, using this code results in an unoptimal dynamicity chain and uneffective partial evaluation:

![alt](dynamicity_case.drawio.svg)

_Scenario 1: As a `LocationHolder` is unique per object instance/method invocation/ etc., the reference to it is highly dynamic. The `LocationDescriptor` is only unique per object type/method definiton, but can only be reached through a dynamic chain._

To make this more effective, we have to hold a separate reference to a `LocationDescriptor`. As every location-accessing instruction (in the implemented subset of .NET) will always use the same `LocationDescriptor`, this results in effective partial evaluation. The new implementation looks like this:

```Java 
public class LocationHolder {

    private final Object[] refs;
    private final long[] primitives;

    public LocationsHolder(int refCount, int primitiveCount) {
        refs = new Object[refCount];
        primitives = new long[primitiveCount];
    }
}

//Accessing a field of an object
//objectType for an instruction never changes!
Object fieldValue = objectType.getLocationsDescriptor().locationToObject(((StaticObject)object).getLocationsHolder(), 0);
```

![alt](dynamicity_case_good.drawio.svg)

_Scenario 2: While the `LocationHolder` remains accessible from a highly dynamic chain, the `LocationDescriptor` is accessible through a static chain. This means the bottom chain will be partially evaluated._