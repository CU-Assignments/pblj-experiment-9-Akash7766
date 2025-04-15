import jakarta.persistence.*;
import org.hibernate.*;
import org.hibernate.cfg.Configuration;

import java.util.List;

public class MainApp {

    // --- Entity class ---
    @Entity
    @Table(name = "students")
    public static class Student {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private int id;

        private String name;
        private int age;

        public Student() {}

        public Student(String name, int age) {
            this.name = name;
            this.age = age;
        }

        public int getId() { return id; }
        public void setId(int id) { this.id = id; }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public int getAge() { return age; }
        public void setAge(int age) { this.age = age; }

        @Override
        public String toString() {
            return "Student{id=" + id + ", name='" + name + "', age=" + age + '}';
        }
    }

    // --- Hibernate Utility ---
    public static class HibernateUtil {
        private static final SessionFactory sessionFactory;

        static {
            try {
                sessionFactory = new Configuration().configure().buildSessionFactory();
            } catch (Throwable ex) {
                throw new ExceptionInInitializerError(ex);
            }
        }

        public static SessionFactory getSessionFactory() {
            return sessionFactory;
        }
    }

    // --- DAO class ---
    public static class StudentDAO {

        public void createStudent(Student student) {
            Transaction tx = null;
            try (Session session = HibernateUtil.getSessionFactory().openSession()) {
                tx = session.beginTransaction();
                session.save(student);
                tx.commit();
            } catch (Exception e) {
                if (tx != null) tx.rollback();
                e.printStackTrace();
            }
        }

        public Student readStudent(int id) {
            try (Session session = HibernateUtil.getSessionFactory().openSession()) {
                return session.get(Student.class, id);
            }
        }

        public List<Student> readAllStudents() {
            try (Session session = HibernateUtil.getSessionFactory().openSession()) {
                return session.createQuery("from Student", Student.class).list();
            }
        }

        public void updateStudent(Student student) {
            Transaction tx = null;
            try (Session session = HibernateUtil.getSessionFactory().openSession()) {
                tx = session.beginTransaction();
                session.update(student);
                tx.commit();
            } catch (Exception e) {
                if (tx != null) tx.rollback();
                e.printStackTrace();
            }
        }

        public void deleteStudent(int id) {
            Transaction tx = null;
            try (Session session = HibernateUtil.getSessionFactory().openSession()) {
                tx = session.beginTransaction();
                Student student = session.get(Student.class, id);
                if (student != null) {
                    session.delete(student);
                }
                tx.commit();
            } catch (Exception e) {
                if (tx != null) tx.rollback();
                e.printStackTrace();
            }
        }
    }

    // --- Main method to test CRUD ---
    public static void main(String[] args) {
        StudentDAO dao = new StudentDAO();

        // Create
        Student s1 = new Student("Alice", 22);
        Student s2 = new Student("Bob", 24);
        dao.createStudent(s1);
        dao.createStudent(s2);

        // Read
        System.out.println("Reading student with ID 1:");
        Student student = dao.readStudent(1);
        System.out.println(student);

        // Update
        System.out.println("Updating student with ID 1:");
        if (student != null) {
            student.setAge(25);
            dao.updateStudent(student);
        }

        // Read All
        System.out.println("All students:");
        dao.readAllStudents().forEach(System.out::println);

        // Delete
        System.out.println("Deleting student with ID 2:");
        dao.deleteStudent(2);
    }
}
