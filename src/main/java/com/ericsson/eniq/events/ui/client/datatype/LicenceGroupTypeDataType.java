package com.ericsson.eniq.events.ui.client.datatype;

public class LicenceGroupTypeDataType {

    private final String id;
    private final String name;

    public LicenceGroupTypeDataType(final String id, final String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) { return true;  }
        if (o == null || getClass() != o.getClass()){  return false; }

        final LicenceGroupTypeDataType that = (LicenceGroupTypeDataType) o;

        if (id != null ? !id.equals(that.id) : that.id != null) {
            return false;
        }

        if (name != null ? !name.equals(that.name) : that.name != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        return result;
    }

    @Override public String toString() {
        final StringBuilder sb = new StringBuilder(64);
        sb.append("LicenceGroupTypeDataType");
        sb.append("{id='").append(id).append('\'');
        sb.append(", name='").append(name).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
