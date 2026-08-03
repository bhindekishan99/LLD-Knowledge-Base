package repository;

import models.Group;

import java.util.HashMap;
import java.util.Map;

public class InMemoryGroupRepository implements GroupRepository {

    private final Map<String, Group> groups = new HashMap<>();

    @Override
    public Group findById(String groupId) {
        return groups.get(groupId);
    }

    @Override
    public void save(Group group) {
        groups.put(group.getId(), group);
    }

    @Override
    public void delete(String groupId) {
        groups.remove(groupId);
    }
}