package alexp.macrobase.evaluation;

import com.google.common.collect.Streams;
import java.util.*;
import java.util.stream.Collectors;

public class GridSearch {

    @FunctionalInterface
    public interface RunInstance {
        double accept(Map<String, Object> params) throws Exception;
    }

    private class SearchParam {
        final String name;
        final Object[] values;

        SearchParam(String name, Object[] values) {
            this.name = name;
            this.values = values;
        }
    }

    private ArrayList<SearchParam> searchParams = new ArrayList<>();

    private SortedMap<Double, Map<String, Object>> results = new TreeMap<>();

    public GridSearch addParam(String name, Object[] values) {
        searchParams.add(new SearchParam(name, values));
        return this;
    }

    public void run(RunInstance runInstance) throws Exception {
        List<Object[]> permutations = new ArrayList<>();
        generatePermutations(searchParams.stream().map(p -> p.values).collect(Collectors.toList()), permutations, 0, new ArrayList<>());

        results.clear();

        for (Object[] permutation : permutations) {
            // {"p1": x, "p2": y, ...}
            Map<String, Object> params = Streams.mapWithIndex(Arrays.stream(permutation), (o, i) -> new AbstractMap.SimpleEntry<>(searchParams.get((int) i).name, o))
                    .collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue));

            results.put(runInstance.accept(params), params);
        }
    }

    public SortedMap<Double, Map<String, Object>> getResults() {
        return results;
    }

    private void generatePermutations(List<Object[]> paramsLists, List<Object[]> result, int depth, List<Object> current) {
        if(depth == paramsLists.size())
        {
            result.add(current.toArray());
            return;
        }

        for(int i = 0; i < paramsLists.get(depth).length; ++i)
        {
            List<Object> next = new ArrayList<>(current);
            next.add(paramsLists.get(depth)[i]);
            generatePermutations(paramsLists, result, depth + 1, next);
        }
    }
}
