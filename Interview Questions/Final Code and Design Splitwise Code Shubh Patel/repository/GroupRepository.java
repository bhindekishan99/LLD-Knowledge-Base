package repository;

import models.Group;

public interface GroupRepository {

    Group findById(String groupId);

    void save(Group group);

    void delete(String groupId);
}