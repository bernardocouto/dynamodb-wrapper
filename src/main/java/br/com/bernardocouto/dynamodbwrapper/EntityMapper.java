package br.com.bernardocouto.dynamodbwrapper;

import br.com.bernardocouto.dynamodbwrapper.annotations.PartitionKey;
import br.com.bernardocouto.dynamodbwrapper.annotations.SortKey;
import br.com.bernardocouto.dynamodbwrapper.annotations.Table;
import br.com.bernardocouto.dynamodbwrapper.converters.Converter;
import br.com.bernardocouto.dynamodbwrapper.utils.TypeInformationUtil;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class EntityMapper {

    private final Map<TypeInformationUtil<?>, Optional<Entity>> entities = new HashMap<>();

    private final Converter converter;

    public EntityMapper(Converter converter) {
        this.converter = converter;
    }

    private Entity createEntity(Class<?> type) {
        Table table = type.getAnnotation(Table.class);
        String partitionKey = null;
        String sortKey = null;
        for (Field field : type.getDeclaredFields()) {
            PartitionKey partitionKeyInstance = field.getAnnotation(PartitionKey.class);
            if (Objects.nonNull(partitionKeyInstance)) {
                partitionKey = partitionKeyInstance.value();
                if (StringUtils.isEmpty(partitionKey)) {
                    partitionKey = converter.translateFieldName(field.getName());
                }
            }
            SortKey sortKeyInstance = field.getAnnotation(SortKey.class);
            if (Objects.nonNull(sortKeyInstance)) {
                sortKey = sortKeyInstance.value();
                if (StringUtils.isEmpty(sortKey)) {
                    sortKey = converter.translateFieldName(field.getName());
                }
            }
        }
        if (StringUtils.isEmpty(partitionKey)) {
            throw new IllegalArgumentException(String.format("Class %s must contain at least one field annotated with PartitionKey", type));
        }
        return new Entity(table.value(), partitionKey, sortKey);
    }

    public Entity getEntity(Class<?> type) {
        return getEntity(TypeInformationUtil.from(type));
    }

    private Entity getEntity(TypeInformationUtil<?> typeInformation) {
        Optional<Entity> entity = entities.get(typeInformation);
        if (Objects.nonNull(entity)) {
            return entity.orElse(null);
        }
        Entity createEntity = createEntity(typeInformation.getType());
        entities.put(typeInformation, Optional.of(createEntity));
        return createEntity;
    }

}
