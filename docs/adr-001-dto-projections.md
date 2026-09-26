# ADR 001: Solving the N+1 Query Issue Using @EntityGraph

## Status

Accepted

## Context

GET /api/tasks endpoint provides a paginated list of tasks. Each task contains information about its assignee (email) and project id. Initially, there was no special handling of the Task.assignee (@ManyToOne) and Task.project (@ManyToOne) relationships, meaning that Hibernate performed lazy loading.

Therefore, fetching a paginated task list resulted in N+1 issue: when fetching a list of 50 items we had 1 query to fetch a list + additional up to 50 queries to resolve each item's assignee relationship + another 50 queries to resolve each item's project relationship – all in all up to 101 queries per API call.

## Decision

@EntityGraph(attributePaths = {"assignee", "project"}) annotation added to TaskRepository.findAll(Pageable). It makes Hibernate fetch tasks along with their assignee and project in 1 query, using LEFT JOIN / JOIN, instead of lazy resolution of each relationship separately.

## Alternatives Considered

- DTO projection query (using JPQL with constructor expression): similar to @EntityGraph solution but more complex to maintain. It would require a manually updated JPQL query containing DTO field list and wouldn't work for any DTO changes.
- Setting relationships to be EAGER loaded by default: not acceptable because it would affect every operation

## Alternative Approaches

- DTO projection query using JPQL + constructor expression: would have produced a similar one-query solution without loading any entities, but required maintaining a JPQL query string manually, which would be fragile in terms of synchronization with DTO evolution.
- Modification of relationships to be eager loaded by default: dismissed because it would introduce a join operation in all queries using Task, while in most cases the join is unnecessary and not wanted.

## Consequences/Effects

- The tasks list endpoint will run in 1 query whatever page size it is, instead of having a scaling effect depending on the number of returned rows.
- @EntityGraph will be applied only to this particular repository method and will not affect other queries using Task entities in terms of loading strategy.
- This was confirmed by running queries with spring.jpa.show-sql=true and counting how many there were in total: from around 101 to 1.