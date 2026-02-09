package pl.wsb.fitnesstracker.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import pl.wsb.fitnesstracker.event.Event;

import java.util.List;

@Repository
public class EventRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public List<Event> findByNameContains(String fragment) {
        return entityManager.createQuery(
                        "select e from Event e where lower(e.name) like lower(concat('%', :fragment, '%'))",
                        Event.class
                )
                .setParameter("fragment", fragment)
                .getResultList();
    }
}