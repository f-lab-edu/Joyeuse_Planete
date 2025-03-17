package com.f_lab.joyeuse_planete.foods.repository;

import co.elastic.clients.elasticsearch._types.SortOptions;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch._types.query_dsl.QueryBuilders;
import com.f_lab.joyeuse_planete.foods.document.FoodDocument;
import com.f_lab.joyeuse_planete.foods.dto.request.FoodSearchCondition;
import com.f_lab.joyeuse_planete.foods.dto.response.FoodDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;

import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static java.util.stream.Collectors.toList;

@Slf4j
@RequiredArgsConstructor
public class FoodCustomRepositoryImpl implements FoodCustomRepository {

  private final ElasticsearchOperations operations;

  private static final Map<String, SortOptions> foodSortByMap = Map.of(
      "RATE_HIGH", new SortOptions.Builder().field(f -> f.field("rate").order(SortOrder.Desc)).build(),
      "PRICE_HIGH", new SortOptions.Builder().field(f -> f.field("price").order(SortOrder.Desc)).build(),
      "PRICE_LOW", new SortOptions.Builder().field(f -> f.field("price").order(SortOrder.Asc)).build()
  );

  private static final Query DEFAULT_FILTER = QueryBuilders.term().field("is_deleted").value(false).build()._toQuery();
  private static final List<String> SEARCH_FIELDS = List.of("food_name", "store_name", "tags");
  private static final String PRICE = "price";
  private static final String COLLECTION_END_TIME = "collection_end_time";
  private static final String COLLECTION_START_TIME = "collection_start_time";
  private static final int SEARCH_MINUTE_THRESHOLD = 20;

  public Page<FoodDTO> getFoodList(FoodSearchCondition condition, Pageable pageable) {
    NativeQuery query = NativeQuery.builder()
        .withQuery(createQuery(condition))
        .withPageable(pageable)
        .withSort(createSortBy(condition))
        .build();

    SearchHits<FoodDocument> searchHits = operations.search(query, FoodDocument.class);

    List<FoodDTO> result = searchHits.stream()
        .map(s -> FoodDTO.from(s.getContent()))
        .collect(toList());

    return new PageImpl<>(result, pageable, searchHits.getTotalHits());
  }

  private Query createQuery(FoodSearchCondition condition) {
    // query 생성
    return createFilter(QueryBuilders.bool(), condition)
        .must(createMultiMatchQuery(condition)).build()._toQuery();
  }

  private BoolQuery.Builder createFilter(BoolQuery.Builder boolQuery, FoodSearchCondition condition) {

    // 최소 값 filter
    addFilterIfNotNull(boolQuery, condition.getMinCost(),
        (v) -> QueryBuilders.range()
            .number(n -> n
                .field(PRICE)
                .gte(v.setScale(4, RoundingMode.HALF_UP).doubleValue()))
            .build()
            ._toQuery());

    // 최고 값 filter
    addFilterIfNotNull(boolQuery, condition.getMaxCost(),
        (v) -> QueryBuilders.range()
            .number(n -> n
                .field(PRICE)
                .lte(v.setScale(4, RoundingMode.HALF_UP).doubleValue()))
            .build()
            ._toQuery());

    // 금일 00시 00분 기준 부터
    addFilterIfNotNull(boolQuery, LocalDateTime.now().toLocalDate().atStartOfDay(),
        (v) -> QueryBuilders.range()
            .date(d -> d
                .field(COLLECTION_START_TIME)
                .gte(v.toString()))
            .build()
            ._toQuery());

    // 픽업시간이 현재 시간 기준 +20분 까지
    addFilterIfNotNull(boolQuery, LocalDateTime.now().plusMinutes(SEARCH_MINUTE_THRESHOLD),
        (v) -> QueryBuilders.range()
            .date(d -> d
                .field(COLLECTION_END_TIME)
                .lt(v.toString()))
            .build()
            ._toQuery());

    boolQuery.filter(DEFAULT_FILTER);

    return boolQuery;
  }

  private <T> void addFilterIfNotNull(BoolQuery.Builder boolQueryBuilder, T value,
                                      Function<T, Query> filterFunction) {

    if (value != null)
      boolQueryBuilder.filter(filterFunction.apply(value));
  }

  private Query createMultiMatchQuery(FoodSearchCondition condition) {
    return (condition.getSearch() != null)
        ? QueryBuilders.multiMatch()
          .fields(SEARCH_FIELDS)
          .query(condition.getSearch())
          .build()
          ._toQuery()

        : QueryBuilders.matchAll().build()._toQuery();
  }

  private List<SortOptions> createSortBy(FoodSearchCondition condition) {
    List<SortOptions> sortBy = condition.getSortBy().stream()
        .map(c -> foodSortByMap.getOrDefault(c, null))
        .collect(toList());

    return !sortBy.isEmpty()
        ? sortBy
        : List.of(foodSortByMap.get("RATE_HIGH"));
  }
}
