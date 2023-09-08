package whu.edu.cn.util;

import org.springframework.stereotype.Component;
import scala.collection.JavaConversions;
import scala.collection.immutable.Map$;
import whu.edu.cn.core.entity.SubsetQuery;
import whu.edu.cn.entity.coverage.Subset;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Component
public class TypeUtil {

    /**
     * Converts a Java HashMap to an immutable Scala Map.
     * The method uses the JavaConversions.mapAsScalaMap method to convert the Java HashMap to a mutable Scala Map.
     * It then creates a new immutable Scala Map from the mutable Scala Map.
     * The method returns the new immutable Scala Map.
     * @param javaHashMap the Java HashMap to convert
     * @return an immutable Scala Map representing the same key-value pairs as the Java HashMap
     */
    public  scala.collection.immutable.Map<String,String> JavaMapToScala(HashMap<String,String> javaHashMap) {
        scala.collection.mutable.Map scalaMap = JavaConversions.mapAsScalaMap(javaHashMap);
        Object objTest = Map$.MODULE$.<String,String>newBuilder().$plus$plus$eq(scalaMap.toSeq());
        Object resultTest = ((scala.collection.mutable.Builder) objTest).result();
        return (scala.collection.immutable.Map<String,String>) (scala.collection.immutable.Map)resultTest;
    }

    /**
     * Converts data of different types based on a specified type.
     * The method takes two parameters: a value and a type.
     * If the value is a String, the method checks the type and converts the String to the corresponding type (int, float, or double).
     * Otherwise, the method returns null.
     * @param value the value to convert
     * @param type the type to convert the value to (int, float, or double)
     * @return the converted value, or null if the value is not a String
     */
    public Object convertDataByType(Object value, String type){
        if(value instanceof String){
            if(type.contains("int")){
                return Integer.parseInt((String) value);
            }else if(type.contains("float")){
                return Float.parseFloat((String) value);
            }else if(type.contains("double")){
                return Double.parseDouble((String) value);
            }else{
                return value;
            }
        }else {
            return null;
        }
    }

    /**
     * Converts a Subset object to a SubsetQuery (Scala class from additional package) object.
     * The method creates a new SubsetQuery object and sets its properties to the corresponding properties of the Subset object.
     * The method returns the new SubsetQuery object.
     * @param subset the Subset object to convert
     * @return a new SubsetQuery object with the same properties as the Subset object
     */
    public SubsetQuery subset2SubsetQuery(Subset subset){
        SubsetQuery subsetQuery = new SubsetQuery();
        subsetQuery.setAxisName(subset.getAxisName());
        subsetQuery.setHighPoint(subset.getHighPoint());
        subsetQuery.setLowerPoint(subset.getLowPoint());
        subsetQuery.setInterval(subset.isInterval());
        subsetQuery.setPoint(subset.getPoint());
        subsetQuery.setIsNumber(subset.isNumber());
        return subsetQuery;
    }

    /**
     * Converts a list of Subset objects to a list of SubsetQuery objects.
     * @param subsetList the list of Subset objects to convert
     * @return a new list of SubsetQuery objects (Scala class from additional package) with the same properties as the Subset objects
     */
    public List<SubsetQuery> subsetList2SubsetQueryList(List<Subset> subsetList){
        if(subsetList == null) return null;
        List<SubsetQuery> subsetQueryList = new ArrayList<>();
        for(Subset subset : subsetList){
            subsetQueryList.add(subset2SubsetQuery(subset));
        }
        return subsetQueryList;
    }
}
