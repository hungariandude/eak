package hu.elte.iszraai.webshop.server.data;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;

import hu.elte.iszraai.webshop.server.data.entities.Item;

public class DB {

    public static void main(final String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("shopping");
        try {
            EntityManager em = emf.createEntityManager();
            try {
                // EntityTransaction tx = em.getTransaction();
                // tx.begin();
                // Item item = new Item(123, "apple", 210);
                // em.persist(item);
                // tx.commit();

                TypedQuery<Item> query = em.createQuery("from Item item", Item.class);
                System.out.println(query.getResultList());
            } finally {
                em.close();
            }
        } finally {
            emf.close();
        }
    }

}
