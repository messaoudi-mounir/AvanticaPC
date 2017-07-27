/**
 * Autogenerated by Avro
 * 
 * DO NOT EDIT DIRECTLY
 */
package Petrolink.WITSML.Events;  
@SuppressWarnings("all")
@org.apache.avro.specific.AvroGenerated
public class ChannelDataAppended extends org.apache.avro.specific.SpecificRecordBase implements org.apache.avro.specific.SpecificRecord {
  public static final org.apache.avro.Schema SCHEMA$ = new org.apache.avro.Schema.Parser().parse("{\"type\":\"record\",\"name\":\"ChannelDataAppended\",\"namespace\":\"Petrolink.WITSML.Events\",\"fields\":[{\"name\":\"data\",\"type\":{\"type\":\"array\",\"items\":{\"type\":\"record\",\"name\":\"ChannelDataItem\",\"namespace\":\"Petrolink.WITSML.Datatypes\",\"fields\":[{\"name\":\"id\",\"type\":{\"type\":\"fixed\",\"name\":\"UUID\",\"namespace\":\"Petrolink.Datatypes\",\"size\":16,\"fullName\":\"Petrolink.Datatypes.UUID\",\"depends\":[]}},{\"name\":\"item\",\"type\":{\"type\":\"record\",\"name\":\"DataItem\",\"fields\":[{\"name\":\"indexes\",\"type\":{\"type\":\"array\",\"items\":{\"type\":\"record\",\"name\":\"IndexValue\",\"namespace\":\"Petrolink.Datatypes\",\"fields\":[{\"name\":\"item\",\"type\":[{\"type\":\"record\",\"name\":\"DateTime\",\"fields\":[{\"name\":\"time\",\"type\":\"long\"},{\"name\":\"offset\",\"type\":\"double\"}],\"fullName\":\"Petrolink.Datatypes.DateTime\",\"depends\":[]},\"double\",\"long\"]}],\"fullName\":\"Petrolink.Datatypes.IndexValue\",\"depends\":[\"Petrolink.Datatypes.DateTime\"]}}},{\"name\":\"value\",\"type\":{\"type\":\"record\",\"name\":\"DataValue\",\"namespace\":\"Petrolink.Datatypes\",\"fields\":[{\"name\":\"item\",\"type\":[\"string\",\"bytes\",\"long\",\"double\",\"boolean\",\"UUID\",\"DateTime\",{\"type\":\"record\",\"name\":\"ArrayOfDouble\",\"fields\":[{\"name\":\"values\",\"type\":{\"type\":\"array\",\"items\":\"double\"}}],\"fullName\":\"Petrolink.Datatypes.ArrayOfDouble\",\"depends\":[]},{\"type\":\"record\",\"name\":\"ArrayOfString\",\"fields\":[{\"name\":\"values\",\"type\":{\"type\":\"array\",\"items\":\"string\"}}],\"fullName\":\"Petrolink.Datatypes.ArrayOfString\",\"depends\":[]}]}],\"fullName\":\"Petrolink.Datatypes.DataValue\",\"depends\":[\"Petrolink.Datatypes.UUID\",\"Petrolink.Datatypes.DateTime\",\"Petrolink.Datatypes.ArrayOfDouble\",\"Petrolink.Datatypes.ArrayOfString\"]}},{\"name\":\"valueAttributes\",\"type\":{\"type\":\"array\",\"items\":{\"type\":\"record\",\"name\":\"DataAttribute\",\"namespace\":\"Petrolink.Datatypes\",\"fields\":[{\"name\":\"attributeId\",\"type\":\"int\"},{\"name\":\"attributeValue\",\"type\":\"DataValue\"}],\"fullName\":\"Petrolink.Datatypes.DataAttribute\",\"depends\":[\"Petrolink.Datatypes.DataValue\"]}}}],\"fullName\":\"Petrolink.WITSML.Datatypes.DataItem\",\"depends\":[\"Petrolink.Datatypes.IndexValue\",\"Petrolink.Datatypes.DataValue\",\"Petrolink.Datatypes.DataAttribute\"]}}],\"fullName\":\"Petrolink.WITSML.Datatypes.ChannelDataItem\",\"depends\":[\"Petrolink.Datatypes.UUID\",\"Petrolink.WITSML.Datatypes.DataItem\"]}}}],\"fullName\":\"Petrolink.WITSML.Events.ChannelDataAppended\",\"depends\":[\"Petrolink.WITSML.Datatypes.ChannelDataItem\"]}");
  public static org.apache.avro.Schema getClassSchema() { return SCHEMA$; }
  @Deprecated public java.util.List<Petrolink.WITSML.Datatypes.ChannelDataItem> data;

  /**
   * Default constructor.  Note that this does not initialize fields
   * to their default values from the schema.  If that is desired then
   * one should use <code>newBuilder()</code>. 
   */
  public ChannelDataAppended() {}

  /**
   * All-args constructor.
   */
  public ChannelDataAppended(java.util.List<Petrolink.WITSML.Datatypes.ChannelDataItem> data) {
    this.data = data;
  }

  public org.apache.avro.Schema getSchema() { return SCHEMA$; }
  // Used by DatumWriter.  Applications should not call. 
  public java.lang.Object get(int field$) {
    switch (field$) {
    case 0: return data;
    default: throw new org.apache.avro.AvroRuntimeException("Bad index");
    }
  }
  // Used by DatumReader.  Applications should not call. 
  @SuppressWarnings(value="unchecked")
  public void put(int field$, java.lang.Object value$) {
    switch (field$) {
    case 0: data = (java.util.List<Petrolink.WITSML.Datatypes.ChannelDataItem>)value$; break;
    default: throw new org.apache.avro.AvroRuntimeException("Bad index");
    }
  }

  /**
   * Gets the value of the 'data' field.
   */
  public java.util.List<Petrolink.WITSML.Datatypes.ChannelDataItem> getData() {
    return data;
  }

  /**
   * Sets the value of the 'data' field.
   * @param value the value to set.
   */
  public void setData(java.util.List<Petrolink.WITSML.Datatypes.ChannelDataItem> value) {
    this.data = value;
  }

  /** Creates a new ChannelDataAppended RecordBuilder */
  public static Petrolink.WITSML.Events.ChannelDataAppended.Builder newBuilder() {
    return new Petrolink.WITSML.Events.ChannelDataAppended.Builder();
  }
  
  /** Creates a new ChannelDataAppended RecordBuilder by copying an existing Builder */
  public static Petrolink.WITSML.Events.ChannelDataAppended.Builder newBuilder(Petrolink.WITSML.Events.ChannelDataAppended.Builder other) {
    return new Petrolink.WITSML.Events.ChannelDataAppended.Builder(other);
  }
  
  /** Creates a new ChannelDataAppended RecordBuilder by copying an existing ChannelDataAppended instance */
  public static Petrolink.WITSML.Events.ChannelDataAppended.Builder newBuilder(Petrolink.WITSML.Events.ChannelDataAppended other) {
    return new Petrolink.WITSML.Events.ChannelDataAppended.Builder(other);
  }
  
  /**
   * RecordBuilder for ChannelDataAppended instances.
   */
  public static class Builder extends org.apache.avro.specific.SpecificRecordBuilderBase<ChannelDataAppended>
    implements org.apache.avro.data.RecordBuilder<ChannelDataAppended> {

    private java.util.List<Petrolink.WITSML.Datatypes.ChannelDataItem> data;

    /** Creates a new Builder */
    private Builder() {
      super(Petrolink.WITSML.Events.ChannelDataAppended.SCHEMA$);
    }
    
    /** Creates a Builder by copying an existing Builder */
    private Builder(Petrolink.WITSML.Events.ChannelDataAppended.Builder other) {
      super(other);
      if (isValidValue(fields()[0], other.data)) {
        this.data = data().deepCopy(fields()[0].schema(), other.data);
        fieldSetFlags()[0] = true;
      }
    }
    
    /** Creates a Builder by copying an existing ChannelDataAppended instance */
    private Builder(Petrolink.WITSML.Events.ChannelDataAppended other) {
            super(Petrolink.WITSML.Events.ChannelDataAppended.SCHEMA$);
      if (isValidValue(fields()[0], other.data)) {
        this.data = data().deepCopy(fields()[0].schema(), other.data);
        fieldSetFlags()[0] = true;
      }
    }

    /** Gets the value of the 'data' field */
    public java.util.List<Petrolink.WITSML.Datatypes.ChannelDataItem> getData() {
      return data;
    }
    
    /** Sets the value of the 'data' field */
    public Petrolink.WITSML.Events.ChannelDataAppended.Builder setData(java.util.List<Petrolink.WITSML.Datatypes.ChannelDataItem> value) {
      validate(fields()[0], value);
      this.data = value;
      fieldSetFlags()[0] = true;
      return this; 
    }
    
    /** Checks whether the 'data' field has been set */
    public boolean hasData() {
      return fieldSetFlags()[0];
    }
    
    /** Clears the value of the 'data' field */
    public Petrolink.WITSML.Events.ChannelDataAppended.Builder clearData() {
      data = null;
      fieldSetFlags()[0] = false;
      return this;
    }

    @Override
    public ChannelDataAppended build() {
      try {
        ChannelDataAppended record = new ChannelDataAppended();
        record.data = fieldSetFlags()[0] ? this.data : (java.util.List<Petrolink.WITSML.Datatypes.ChannelDataItem>) defaultValue(fields()[0]);
        return record;
      } catch (Exception e) {
        throw new org.apache.avro.AvroRuntimeException(e);
      }
    }
  }
}