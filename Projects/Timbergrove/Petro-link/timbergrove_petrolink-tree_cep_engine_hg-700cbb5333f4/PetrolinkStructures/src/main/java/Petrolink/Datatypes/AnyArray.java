/**
 * Autogenerated by Avro
 * 
 * DO NOT EDIT DIRECTLY
 */
package Petrolink.Datatypes;  
@SuppressWarnings("all")
@org.apache.avro.specific.AvroGenerated
public class AnyArray extends org.apache.avro.specific.SpecificRecordBase implements org.apache.avro.specific.SpecificRecord {
  public static final org.apache.avro.Schema SCHEMA$ = new org.apache.avro.Schema.Parser().parse("{\"type\":\"record\",\"name\":\"AnyArray\",\"namespace\":\"Petrolink.Datatypes\",\"fields\":[{\"name\":\"item\",\"type\":[\"null\",{\"type\":\"record\",\"name\":\"ArrayOfBoolean\",\"fields\":[{\"name\":\"values\",\"type\":{\"type\":\"array\",\"items\":\"boolean\"}}],\"fullName\":\"Petrolink.Datatypes.ArrayOfBoolean\",\"depends\":[]},\"bytes\",{\"type\":\"record\",\"name\":\"ArrayOfInt\",\"fields\":[{\"name\":\"values\",\"type\":{\"type\":\"array\",\"items\":\"int\"}}],\"fullName\":\"Petrolink.Datatypes.ArrayOfInt\",\"depends\":[]},{\"type\":\"record\",\"name\":\"ArrayOfLong\",\"fields\":[{\"name\":\"values\",\"type\":{\"type\":\"array\",\"items\":\"long\"}}],\"fullName\":\"Petrolink.Datatypes.ArrayOfLong\",\"depends\":[]},{\"type\":\"record\",\"name\":\"ArrayOfFloat\",\"fields\":[{\"name\":\"values\",\"type\":{\"type\":\"array\",\"items\":\"float\"}}],\"fullName\":\"Petrolink.Datatypes.ArrayOfFloat\",\"depends\":[]},{\"type\":\"record\",\"name\":\"ArrayOfDouble\",\"fields\":[{\"name\":\"values\",\"type\":{\"type\":\"array\",\"items\":\"double\"}}],\"fullName\":\"Petrolink.Datatypes.ArrayOfDouble\",\"depends\":[]}]}],\"fullName\":\"Petrolink.Datatypes.AnyArray\",\"depends\":[\"Petrolink.Datatypes.ArrayOfBoolean\",\"Petrolink.Datatypes.ArrayOfInt\",\"Petrolink.Datatypes.ArrayOfLong\",\"Petrolink.Datatypes.ArrayOfFloat\",\"Petrolink.Datatypes.ArrayOfDouble\"]}");
  public static org.apache.avro.Schema getClassSchema() { return SCHEMA$; }
  @Deprecated public java.lang.Object item;

  /**
   * Default constructor.  Note that this does not initialize fields
   * to their default values from the schema.  If that is desired then
   * one should use <code>newBuilder()</code>. 
   */
  public AnyArray() {}

  /**
   * All-args constructor.
   */
  public AnyArray(java.lang.Object item) {
    this.item = item;
  }

  public org.apache.avro.Schema getSchema() { return SCHEMA$; }
  // Used by DatumWriter.  Applications should not call. 
  public java.lang.Object get(int field$) {
    switch (field$) {
    case 0: return item;
    default: throw new org.apache.avro.AvroRuntimeException("Bad index");
    }
  }
  // Used by DatumReader.  Applications should not call. 
  @SuppressWarnings(value="unchecked")
  public void put(int field$, java.lang.Object value$) {
    switch (field$) {
    case 0: item = (java.lang.Object)value$; break;
    default: throw new org.apache.avro.AvroRuntimeException("Bad index");
    }
  }

  /**
   * Gets the value of the 'item' field.
   */
  public java.lang.Object getItem() {
    return item;
  }

  /**
   * Sets the value of the 'item' field.
   * @param value the value to set.
   */
  public void setItem(java.lang.Object value) {
    this.item = value;
  }

  /** Creates a new AnyArray RecordBuilder */
  public static Petrolink.Datatypes.AnyArray.Builder newBuilder() {
    return new Petrolink.Datatypes.AnyArray.Builder();
  }
  
  /** Creates a new AnyArray RecordBuilder by copying an existing Builder */
  public static Petrolink.Datatypes.AnyArray.Builder newBuilder(Petrolink.Datatypes.AnyArray.Builder other) {
    return new Petrolink.Datatypes.AnyArray.Builder(other);
  }
  
  /** Creates a new AnyArray RecordBuilder by copying an existing AnyArray instance */
  public static Petrolink.Datatypes.AnyArray.Builder newBuilder(Petrolink.Datatypes.AnyArray other) {
    return new Petrolink.Datatypes.AnyArray.Builder(other);
  }
  
  /**
   * RecordBuilder for AnyArray instances.
   */
  public static class Builder extends org.apache.avro.specific.SpecificRecordBuilderBase<AnyArray>
    implements org.apache.avro.data.RecordBuilder<AnyArray> {

    private java.lang.Object item;

    /** Creates a new Builder */
    private Builder() {
      super(Petrolink.Datatypes.AnyArray.SCHEMA$);
    }
    
    /** Creates a Builder by copying an existing Builder */
    private Builder(Petrolink.Datatypes.AnyArray.Builder other) {
      super(other);
      if (isValidValue(fields()[0], other.item)) {
        this.item = data().deepCopy(fields()[0].schema(), other.item);
        fieldSetFlags()[0] = true;
      }
    }
    
    /** Creates a Builder by copying an existing AnyArray instance */
    private Builder(Petrolink.Datatypes.AnyArray other) {
            super(Petrolink.Datatypes.AnyArray.SCHEMA$);
      if (isValidValue(fields()[0], other.item)) {
        this.item = data().deepCopy(fields()[0].schema(), other.item);
        fieldSetFlags()[0] = true;
      }
    }

    /** Gets the value of the 'item' field */
    public java.lang.Object getItem() {
      return item;
    }
    
    /** Sets the value of the 'item' field */
    public Petrolink.Datatypes.AnyArray.Builder setItem(java.lang.Object value) {
      validate(fields()[0], value);
      this.item = value;
      fieldSetFlags()[0] = true;
      return this; 
    }
    
    /** Checks whether the 'item' field has been set */
    public boolean hasItem() {
      return fieldSetFlags()[0];
    }
    
    /** Clears the value of the 'item' field */
    public Petrolink.Datatypes.AnyArray.Builder clearItem() {
      item = null;
      fieldSetFlags()[0] = false;
      return this;
    }

    @Override
    public AnyArray build() {
      try {
        AnyArray record = new AnyArray();
        record.item = fieldSetFlags()[0] ? this.item : (java.lang.Object) defaultValue(fields()[0]);
        return record;
      } catch (Exception e) {
        throw new org.apache.avro.AvroRuntimeException(e);
      }
    }
  }
}