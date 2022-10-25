package ch.epfl.tchu.net;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.regex.Pattern;

/**
 * The interface serializing and deserializing a single object of a collection of objects
 * @param <T> The object to serialize or deserialize
 * @author Mamoun Chami (325917)
 * @author Ismael Berrada (327482)
 */
public interface Serde <T> {

    /**
     * Serializes the argument
     * @param object the object to serialize
     * @return the parameter serialized
     */
    String serialize(T object);

    /**
     * Deserializes the argument
     * @param chain the chain to deserialize
     * @return the object that was serialized
     */
    T deserialize(String chain);

    /**
     * a Serde serializing and deserializing the parameter of the class using the functions
     * @param serialisationFunction the function showing how to serialize the parameter T
     * @param deserializationFunction the function showing how to deserialize a chain to a parameter T
     * @param <T> the parameter to be serialized and deserialized by this Serde
     * @return the Serde corresponding to the parameter T
     */
    static <T> Serde<T> of(Function<T, String> serialisationFunction, Function<String, T> deserializationFunction) {
        return new Serde<>() {
            @Override
            public String serialize(T t) {
                return serialisationFunction.apply(t);
            }

            @Override
            public T deserialize(String chain) {
                return deserializationFunction.apply(chain);
            }
        };
    }

    /**
     * a Serde serializing and deserializing a List of a given Object, be it an enum or a List of Routes
     * @param values the List of objects that are going to be serialized and deserialized by the Serde returned
     * @param <T> the type of object to be serialized and deserialized
     * @return the Serde corresponding to the parameter T, member of a List or enum
     */
    static <T> Serde<T> oneOf (List<T> values){
        Preconditions.checkArgument(!values.isEmpty());
        return of(t -> Integer.toString(values.indexOf(t)), chain -> values.get(Integer.parseInt(chain)));
    }

    /**
     *
     * @param serde the serde that serializes and deserializes the parameter T
     * @param delimiter the separator of the values of the list
     * @param <T> the type of objects in the List
     * @return a serde serializing and deserializing a List of an object
     */
    static <T> Serde<List<T>> listOf(Serde<T> serde, String delimiter) {

        return new Serde<>() {
            @Override
            public String serialize(List<T> tValues) {
                if (tValues.isEmpty()) return "";
                List<String> l = new ArrayList<>();
                tValues.forEach(value -> l.add(serde.serialize(value)));
                return String.join(delimiter, l);
            }

            @Override
            public List<T> deserialize(String chain) {
                if (chain.isEmpty()) return List.of();
                String[] sequence = chain.split(Pattern.quote(delimiter), -1);
                List<T> deserialized = new ArrayList<>();
                for (String s : sequence) deserialized.add(serde.deserialize(s));
                return deserialized;
            }
        };
    }

    /**
     *
     * @param serde the Serde that serializes and deserializes the parameter T
     * @param delimiter the separator of the values of the bag
     * @param <T> the type of objects in the bag
     * @return a Serde serializing and deserializing a bag of the parameter T
     */
    static <T extends Comparable<T>> Serde<SortedBag<T>> bagOf(Serde<T> serde, String delimiter){
        return new Serde<>() {
            @Override
            public String serialize(SortedBag<T> sb) {
                return listOf(serde, delimiter).serialize(sb.toList());
            }

            @Override
            public SortedBag<T> deserialize(String chain) {
                return SortedBag.of(listOf(serde, delimiter).deserialize(chain));
            }
        };
    }

}