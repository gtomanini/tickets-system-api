Crie uma nova entidade JPA para o domínio especificado: $ARGUMENTS

Siga exatamente os padrões definidos no CLAUDE.md. Execute as etapas abaixo em ordem:

## 1. Determine o tipo de entidade

A partir de `$ARGUMENTS`, identifique:
- **Nome** da entidade (ex: `Discount`, `EventTag`)
- **Tipo de ID**: use `UUIDIdEntity` se for dado transacional ligado a usuário; use `AutoIncrementIdEntity` para dados de configuração/referência
- **Campos** necessários (se não especificado, peça ao usuário)
- **Relacionamentos** com outras entidades existentes

## 2. Crie a entidade em `models/`

```java
@Entity
@Table(name = "<nome_tabela>")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class <NomeEntidade> extends <AutoIncrementIdEntity|UUIDIdEntity> {
    // campos aqui
}
```

Regras:
- Relacionamentos `@ManyToOne` e `@OneToMany` devem ter `fetch` explícito
- Usar `FetchType.LAZY` por padrão; `EAGER` apenas se justificado
- Campos de texto longos: `@Column(columnDefinition = "TEXT")`
- Valores monetários: `BigDecimal` (nunca `double` para dinheiro)
- Datas/horas: `LocalDateTime`

## 3. Crie o Repository em `repositories/`

```java
public interface <Nome>Repository extends JpaRepository<<Nome>, <TipoId>> {
}
```

Adicionar `JpaSpecificationExecutor<<Nome>>` se a entidade precisará de busca com filtros.

## 4. Verifique relacionamentos

Para cada relacionamento com entidades existentes:
- Adicione o lado inverso na entidade relacionada se necessário (`@OneToMany(mappedBy=...)`)
- Não esqueça de verificar se precisa de `cascade = CascadeType.ALL` em `@OneToMany`

## 5. Liste as migrações necessárias

Descreva a tabela SQL que será gerada (o Hibernate cria automaticamente com `ddl-auto=update`, mas documente aqui para referência):
```sql
-- Tabela gerada automaticamente pelo Hibernate
CREATE TABLE <nome_tabela> (
  id ...,
  created_at DATETIME,
  updated_at DATETIME,
  deleted BOOLEAN DEFAULT FALSE,
  -- outros campos
);
```

## 6. Reporte o resultado

Liste:
- Arquivo da entidade criado
- Arquivo do repository criado
- Relacionamentos adicionados em outras entidades
- Próximos passos sugeridos (criar DTO, service, controller com `/new-endpoint`)
