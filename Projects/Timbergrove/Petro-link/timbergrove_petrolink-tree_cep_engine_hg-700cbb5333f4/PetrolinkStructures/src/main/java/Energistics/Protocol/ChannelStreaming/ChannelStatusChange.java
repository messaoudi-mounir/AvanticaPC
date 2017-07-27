/**
 * Autogenerated by Avro
 * 
 * DO NOT EDIT DIRECTLY
 */
package Energistics.Protocol.ChannelStreaming;  
@SuppressWarnings("all")
@org.apache.avro.specific.AvroGenerated
public class ChannelStatusChange extends org.apache.avro.specific.SpecificRecordBase implements org.apache.avro.specific.SpecificRecord {
  public static final org.apache.avro.Schema SCHEMA$ = new org.apache.avro.Schema.Parser().parse("{\"type\":\"record\",\"name\":\"ChannelStatusChange\",\"namespace\":\"Energistics.Protocol.ChannelStreaming\",\"fields\":[{\"name\":\"channelId\",\"type\":\"long\"},{\"name\":\"status\",\"type\":{\"type\":\"enum\",\"name\":\"ChannelStatuses\",\"namespace\":\"Energistics.Datatypes.ChannelData\",\"symbols\":[\"Active\",\"Inactive\",\"Closed\"],\"fullName\":\"Energistics.Datatypes.ChannelData.ChannelStatuses\",\"depends\":[]}}],\"messageType\":\"10\",\"protocol\":\"1\",\"senderRole\":\"producer\",\"protocolRoles\":\"producer,consumer\",\"fullName\":\"Energistics.Protocol.ChannelStreaming.ChannelStatusChange\",\"depends\":[\"Energistics.Datatypes.ChannelData.ChannelStatuses\"]}");
  public static org.apache.avro.Schema getClassSchema() { return SCHEMA$; }
  @Deprecated public long channelId;
  @Deprecated public Energistics.Datatypes.ChannelData.ChannelStatuses status;

  /**
   * Default constructor.  Note that this does not initialize fields
   * to their default values from the schema.  If that is desired then
   * one should use <code>newBuilder()</code>. 
   */
  public ChannelStatusChange() {}

  /**
   * All-args constructor.
   */
  public ChannelStatusChange(java.lang.Long channelId, Energistics.Datatypes.ChannelData.ChannelStatuses status) {
    this.channelId = channelId;
    this.status = status;
  }

  public org.apache.avro.Schema getSchema() { return SCHEMA$; }
  // Used by DatumWriter.  Applications should not call. 
  public java.lang.Object get(int field$) {
    switch (field$) {
    case 0: return channelId;
    case 1: return status;
    default: throw new org.apache.avro.AvroRuntimeException("Bad index");
    }
  }
  // Used by DatumReader.  Applications should not call. 
  @SuppressWarnings(value="unchecked")
  public void put(int field$, java.lang.Object value$) {
    switch (field$) {
    case 0: channelId = (java.lang.Long)value$; break;
    case 1: status = (Energistics.Datatypes.ChannelData.ChannelStatuses)value$; break;
    default: throw new org.apache.avro.AvroRuntimeException("Bad index");
    }
  }

  /**
   * Gets the value of the 'channelId' field.
   */
  public java.lang.Long getChannelId() {
    return channelId;
  }

  /**
   * Sets the value of the 'channelId' field.
   * @param value the value to set.
   */
  public void setChannelId(java.lang.Long value) {
    this.channelId = value;
  }

  /**
   * Gets the value of the 'status' field.
   */
  public Energistics.Datatypes.ChannelData.ChannelStatuses getStatus() {
    return status;
  }

  /**
   * Sets the value of the 'status' field.
   * @param value the value to set.
   */
  public void setStatus(Energistics.Datatypes.ChannelData.ChannelStatuses value) {
    this.status = value;
  }

  /** Creates a new ChannelStatusChange RecordBuilder */
  public static Energistics.Protocol.ChannelStreaming.ChannelStatusChange.Builder newBuilder() {
    return new Energistics.Protocol.ChannelStreaming.ChannelStatusChange.Builder();
  }
  
  /** Creates a new ChannelStatusChange RecordBuilder by copying an existing Builder */
  public static Energistics.Protocol.ChannelStreaming.ChannelStatusChange.Builder newBuilder(Energistics.Protocol.ChannelStreaming.ChannelStatusChange.Builder other) {
    return new Energistics.Protocol.ChannelStreaming.ChannelStatusChange.Builder(other);
  }
  
  /** Creates a new ChannelStatusChange RecordBuilder by copying an existing ChannelStatusChange instance */
  public static Energistics.Protocol.ChannelStreaming.ChannelStatusChange.Builder newBuilder(Energistics.Protocol.ChannelStreaming.ChannelStatusChange other) {
    return new Energistics.Protocol.ChannelStreaming.ChannelStatusChange.Builder(other);
  }
  
  /**
   * RecordBuilder for ChannelStatusChange instances.
   */
  public static class Builder extends org.apache.avro.specific.SpecificRecordBuilderBase<ChannelStatusChange>
    implements org.apache.avro.data.RecordBuilder<ChannelStatusChange> {

    private long channelId;
    private Energistics.Datatypes.ChannelData.ChannelStatuses status;

    /** Creates a new Builder */
    private Builder() {
      super(Energistics.Protocol.ChannelStreaming.ChannelStatusChange.SCHEMA$);
    }
    
    /** Creates a Builder by copying an existing Builder */
    private Builder(Energistics.Protocol.ChannelStreaming.ChannelStatusChange.Builder other) {
      super(other);
      if (isValidValue(fields()[0], other.channelId)) {
        this.channelId = data().deepCopy(fields()[0].schema(), other.channelId);
        fieldSetFlags()[0] = true;
      }
      if (isValidValue(fields()[1], other.status)) {
        this.status = data().deepCopy(fields()[1].schema(), other.status);
        fieldSetFlags()[1] = true;
      }
    }
    
    /** Creates a Builder by copying an existing ChannelStatusChange instance */
    private Builder(Energistics.Protocol.ChannelStreaming.ChannelStatusChange other) {
            super(Energistics.Protocol.ChannelStreaming.ChannelStatusChange.SCHEMA$);
      if (isValidValue(fields()[0], other.channelId)) {
        this.channelId = data().deepCopy(fields()[0].schema(), other.channelId);
        fieldSetFlags()[0] = true;
      }
      if (isValidValue(fields()[1], other.status)) {
        this.status = data().deepCopy(fields()[1].schema(), other.status);
        fieldSetFlags()[1] = true;
      }
    }

    /** Gets the value of the 'channelId' field */
    public java.lang.Long getChannelId() {
      return channelId;
    }
    
    /** Sets the value of the 'channelId' field */
    public Energistics.Protocol.ChannelStreaming.ChannelStatusChange.Builder setChannelId(long value) {
      validate(fields()[0], value);
      this.channelId = value;
      fieldSetFlags()[0] = true;
      return this; 
    }
    
    /** Checks whether the 'channelId' field has been set */
    public boolean hasChannelId() {
      return fieldSetFlags()[0];
    }
    
    /** Clears the value of the 'channelId' field */
    public Energistics.Protocol.ChannelStreaming.ChannelStatusChange.Builder clearChannelId() {
      fieldSetFlags()[0] = false;
      return this;
    }

    /** Gets the value of the 'status' field */
    public Energistics.Datatypes.ChannelData.ChannelStatuses getStatus() {
      return status;
    }
    
    /** Sets the value of the 'status' field */
    public Energistics.Protocol.ChannelStreaming.ChannelStatusChange.Builder setStatus(Energistics.Datatypes.ChannelData.ChannelStatuses value) {
      validate(fields()[1], value);
      this.status = value;
      fieldSetFlags()[1] = true;
      return this; 
    }
    
    /** Checks whether the 'status' field has been set */
    public boolean hasStatus() {
      return fieldSetFlags()[1];
    }
    
    /** Clears the value of the 'status' field */
    public Energistics.Protocol.ChannelStreaming.ChannelStatusChange.Builder clearStatus() {
      status = null;
      fieldSetFlags()[1] = false;
      return this;
    }

    @Override
    public ChannelStatusChange build() {
      try {
        ChannelStatusChange record = new ChannelStatusChange();
        record.channelId = fieldSetFlags()[0] ? this.channelId : (java.lang.Long) defaultValue(fields()[0]);
        record.status = fieldSetFlags()[1] ? this.status : (Energistics.Datatypes.ChannelData.ChannelStatuses) defaultValue(fields()[1]);
        return record;
      } catch (Exception e) {
        throw new org.apache.avro.AvroRuntimeException(e);
      }
    }
  }
}