/**
 * Autogenerated by Avro
 * 
 * DO NOT EDIT DIRECTLY
 */
package Energistics.Protocol.StoreNotification;  
@SuppressWarnings("all")
@org.apache.avro.specific.AvroGenerated
public class NotificationRequest extends org.apache.avro.specific.SpecificRecordBase implements org.apache.avro.specific.SpecificRecord {
  public static final org.apache.avro.Schema SCHEMA$ = new org.apache.avro.Schema.Parser().parse("{\"type\":\"record\",\"name\":\"NotificationRequest\",\"namespace\":\"Energistics.Protocol.StoreNotification\",\"fields\":[{\"name\":\"request\",\"type\":{\"type\":\"record\",\"name\":\"NotificationRequestRecord\",\"namespace\":\"Energistics.Datatypes.Object\",\"fields\":[{\"name\":\"uri\",\"type\":\"string\"},{\"name\":\"uuid\",\"type\":\"string\"},{\"name\":\"includeObjectData\",\"type\":\"boolean\"},{\"name\":\"startTime\",\"type\":\"long\"},{\"name\":\"objectTypes\",\"type\":{\"type\":\"array\",\"items\":\"string\"}}],\"fullName\":\"Energistics.Datatypes.Object.NotificationRequestRecord\",\"depends\":[]}}],\"messageType\":\"1\",\"protocol\":\"5\",\"senderRole\":\"customer\",\"protocolRoles\":\"store,customer\",\"fullName\":\"Energistics.Protocol.StoreNotification.NotificationRequest\",\"depends\":[\"Energistics.Datatypes.Object.NotificationRequestRecord\"]}");
  public static org.apache.avro.Schema getClassSchema() { return SCHEMA$; }
  @Deprecated public Energistics.Datatypes.Object.NotificationRequestRecord request;

  /**
   * Default constructor.  Note that this does not initialize fields
   * to their default values from the schema.  If that is desired then
   * one should use <code>newBuilder()</code>. 
   */
  public NotificationRequest() {}

  /**
   * All-args constructor.
   */
  public NotificationRequest(Energistics.Datatypes.Object.NotificationRequestRecord request) {
    this.request = request;
  }

  public org.apache.avro.Schema getSchema() { return SCHEMA$; }
  // Used by DatumWriter.  Applications should not call. 
  public java.lang.Object get(int field$) {
    switch (field$) {
    case 0: return request;
    default: throw new org.apache.avro.AvroRuntimeException("Bad index");
    }
  }
  // Used by DatumReader.  Applications should not call. 
  @SuppressWarnings(value="unchecked")
  public void put(int field$, java.lang.Object value$) {
    switch (field$) {
    case 0: request = (Energistics.Datatypes.Object.NotificationRequestRecord)value$; break;
    default: throw new org.apache.avro.AvroRuntimeException("Bad index");
    }
  }

  /**
   * Gets the value of the 'request' field.
   */
  public Energistics.Datatypes.Object.NotificationRequestRecord getRequest() {
    return request;
  }

  /**
   * Sets the value of the 'request' field.
   * @param value the value to set.
   */
  public void setRequest(Energistics.Datatypes.Object.NotificationRequestRecord value) {
    this.request = value;
  }

  /** Creates a new NotificationRequest RecordBuilder */
  public static Energistics.Protocol.StoreNotification.NotificationRequest.Builder newBuilder() {
    return new Energistics.Protocol.StoreNotification.NotificationRequest.Builder();
  }
  
  /** Creates a new NotificationRequest RecordBuilder by copying an existing Builder */
  public static Energistics.Protocol.StoreNotification.NotificationRequest.Builder newBuilder(Energistics.Protocol.StoreNotification.NotificationRequest.Builder other) {
    return new Energistics.Protocol.StoreNotification.NotificationRequest.Builder(other);
  }
  
  /** Creates a new NotificationRequest RecordBuilder by copying an existing NotificationRequest instance */
  public static Energistics.Protocol.StoreNotification.NotificationRequest.Builder newBuilder(Energistics.Protocol.StoreNotification.NotificationRequest other) {
    return new Energistics.Protocol.StoreNotification.NotificationRequest.Builder(other);
  }
  
  /**
   * RecordBuilder for NotificationRequest instances.
   */
  public static class Builder extends org.apache.avro.specific.SpecificRecordBuilderBase<NotificationRequest>
    implements org.apache.avro.data.RecordBuilder<NotificationRequest> {

    private Energistics.Datatypes.Object.NotificationRequestRecord request;

    /** Creates a new Builder */
    private Builder() {
      super(Energistics.Protocol.StoreNotification.NotificationRequest.SCHEMA$);
    }
    
    /** Creates a Builder by copying an existing Builder */
    private Builder(Energistics.Protocol.StoreNotification.NotificationRequest.Builder other) {
      super(other);
      if (isValidValue(fields()[0], other.request)) {
        this.request = data().deepCopy(fields()[0].schema(), other.request);
        fieldSetFlags()[0] = true;
      }
    }
    
    /** Creates a Builder by copying an existing NotificationRequest instance */
    private Builder(Energistics.Protocol.StoreNotification.NotificationRequest other) {
            super(Energistics.Protocol.StoreNotification.NotificationRequest.SCHEMA$);
      if (isValidValue(fields()[0], other.request)) {
        this.request = data().deepCopy(fields()[0].schema(), other.request);
        fieldSetFlags()[0] = true;
      }
    }

    /** Gets the value of the 'request' field */
    public Energistics.Datatypes.Object.NotificationRequestRecord getRequest() {
      return request;
    }
    
    /** Sets the value of the 'request' field */
    public Energistics.Protocol.StoreNotification.NotificationRequest.Builder setRequest(Energistics.Datatypes.Object.NotificationRequestRecord value) {
      validate(fields()[0], value);
      this.request = value;
      fieldSetFlags()[0] = true;
      return this; 
    }
    
    /** Checks whether the 'request' field has been set */
    public boolean hasRequest() {
      return fieldSetFlags()[0];
    }
    
    /** Clears the value of the 'request' field */
    public Energistics.Protocol.StoreNotification.NotificationRequest.Builder clearRequest() {
      request = null;
      fieldSetFlags()[0] = false;
      return this;
    }

    @Override
    public NotificationRequest build() {
      try {
        NotificationRequest record = new NotificationRequest();
        record.request = fieldSetFlags()[0] ? this.request : (Energistics.Datatypes.Object.NotificationRequestRecord) defaultValue(fields()[0]);
        return record;
      } catch (Exception e) {
        throw new org.apache.avro.AvroRuntimeException(e);
      }
    }
  }
}