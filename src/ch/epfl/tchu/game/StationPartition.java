package ch.epfl.tchu.game;
import ch.epfl.tchu.Preconditions;

/**
 * Each station with its representative
 * @author Mamoun Chami (325917)
 * @author Ismael Berrada (327482)
 */
public final class StationPartition implements StationConnectivity {
    private final int[] links;

    @Override
    public boolean connected(Station s1, Station s2) {
        if (s1.id() < links.length && s2.id() < links.length) {
            return links[s1.id()] == links[s2.id()];
        } else return s1.id() == s2.id();

    }

    private StationPartition(int[] links) {
        this.links = links.clone();
    }

    /**
     * The Builder of a Partition
     */
    static public final class Builder {
        private final int[] a;

        /**
         * The constructor of the builder
         * @param stationCount the number of stations in the partition
         * @throws IllegalArgumentException if the stationCount is negative
         */
        public Builder(int stationCount) {
            Preconditions.checkArgument(stationCount >= 0);
            a = new int[stationCount];
            for (int i = 0; i < stationCount; i++) {
                a[i] = i;
            }

        }

        private int representative(int sid) {
            while (a[sid] != sid) {
                sid = a[sid];
            }
            return sid;
        }

        /**
         * Links a station to the representative of another
         * @param s1 the station linked
         * @param s2 the representative of this station is used
         * @return
         */
        public Builder connect(Station s1, Station s2) {
            a[representative(s1.id())] = representative(s2.id());
            return this;
        }

        /**
         * Build into a stationpPartition
         * @return the stationpartition done with this builder
         */
        public StationPartition build() {
            for(int i = 0; i < a.length; i++) {
                a[i] = representative(i);
            }
            return new StationPartition(a);
        }
    }

}

