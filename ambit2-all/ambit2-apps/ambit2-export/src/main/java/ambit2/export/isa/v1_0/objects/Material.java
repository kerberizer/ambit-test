
package ambit2.export.isa.v1_0.objects;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Generated;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonValue;


/**
 * ISA material node schema
 * <p>
 * JSON-schema representing a material node in the ISA model, which is not a source or a sample (as they have specific schemas) - this will correspond to 'Extract Name', 'Labeled Extract Name'
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("org.jsonschema2pojo")
@JsonPropertyOrder({
    "@id",
    "name",
    "type",
    "characteristics",
    "derivesFrom"
})
public class Material {

    @JsonProperty("@id")
    public URI Id;
    @JsonProperty("name")
    public String name;
    @JsonProperty("type")
    public Material.Type type;
    @JsonProperty("characteristics")
    public List<MaterialAttribute> characteristics = new ArrayList<MaterialAttribute>();
    /**
     * ISA material node schema
     * <p>
     * JSON-schema representing a material node in the ISA model, which is not a source or a sample (as they have specific schemas) - this will correspond to 'Extract Name', 'Labeled Extract Name'
     * 
     */
    @JsonProperty("derivesFrom")
    public Material derivesFrom;

    @Generated("org.jsonschema2pojo")
    public static enum Type {

        EXTRACT_NAME("Extract Name"),
        LABELED_EXTRACT_NAME("Labeled Extract Name");
        private final String value;
        private static Map<String, Material.Type> constants = new HashMap<String, Material.Type>();

        static {
            for (Material.Type c: values()) {
                constants.put(c.value, c);
            }
        }

        private Type(String value) {
            this.value = value;
        }

        @JsonValue
        @Override
        public String toString() {
            return this.value;
        }

        @JsonCreator
        public static Material.Type fromValue(String value) {
            Material.Type constant = constants.get(value);
            if (constant == null) {
                throw new IllegalArgumentException(value);
            } else {
                return constant;
            }
        }

    }

}