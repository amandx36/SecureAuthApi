
## Purpose
Represents application users for:
- Email/Password login
- Google OAuth2
- GitHub OAuth2

---

## Core Annotations

### @Entity
- Marks class as JPA entity
- Mapped to a database table

**Interview:** Identifies a persistent JPA entity.

---

### @Table(name = "users")
- Maps entity to `users` table

**Interview:** Used when table name differs from class name.

---

### @Id
- Primary key of table

**Interview:** Identifies unique row in table.

---

### @GeneratedValue(strategy = GenerationType.IDENTITY)
- Auto-increment handled by DB

**Interview:** Uses database identity column.

---

### @Column(unique = true)
- Enforces uniqueness (email)

**Interview:** Prevents duplicate records at DB level.

---

### @Column(name = "profile_picture")
- Custom DB column name

**Interview:** Maps field to different column name.

---

## Enum Mapping

### @Enumerated(EnumType.STRING)
- Stores enum as readable string
- Safer than ORDINAL     like google github and via email
