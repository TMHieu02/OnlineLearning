package src.config.utils;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import jakarta.servlet.http.HttpServletRequest;
import src.config.dto.Pagination;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ApiQuery<T> {
    CriteriaBuilder cb;
    CriteriaQuery<T> cq;
    HttpServletRequest req;
    Root<T> root;
    EntityManager em;
    List<Predicate> predicates = new ArrayList<>();
    private TypedQuery<T> query = null;
    Pagination pagination;

    public ApiQuery(HttpServletRequest req, EntityManager em, Class<T> entityType, Pagination pagination) {
        this.em = em;
        this.cb = em.getCriteriaBuilder();
        this.cq = cb.createQuery(entityType);
        this.req = req;
        this.root = cq.from(entityType);
        this.pagination = pagination;
    }

    public ApiQuery<T> filter() {
        predicates.add(cb.equal(root.get("isDeleted"), false));
        String queryString;
        queryString = URLDecoder.decode(req.getQueryString(), StandardCharsets.UTF_8);
        if (queryString != null) {
            Pattern pattern = Pattern.compile("(?i)(\\w+)\\{\\{(lt|lte|gt|gte|neq|in|search)}}=(.*?)(&|$)");
            Matcher matcher = pattern.matcher(queryString);
            while (matcher.find()) {
                switch (matcher.group(2)) {
                    case "lt" -> predicates.add(cb.lt(root.get(matcher.group(1)), Integer.parseInt(matcher.group(3))));
                    case "lte" -> predicates.add(cb.lessThanOrEqualTo(root.get(matcher.group(1)), Integer.parseInt(matcher.group(3))));
                    case "gt" -> predicates.add(cb.gt(root.get(matcher.group(1)), Integer.parseInt(matcher.group(3))));
                    case "gte" -> predicates.add(cb.greaterThanOrEqualTo(root.get(matcher.group(1)), Integer.parseInt(matcher.group(3))));
                    case "search" -> predicates.add(cb.like(root.get(matcher.group(1)), "%" + matcher.group(3) + "%"));
                    case "neq" -> {
                        if (matcher.group(3).equals("null")) {
                            predicates.add(cb.isNotNull(root.get(matcher.group(1))));
                        } else {
                            predicates.add(cb.notEqual(root.get(matcher.group(1)), matcher.group(3)));
                        }
                    }
                    case "in" -> {
                        String[] list = matcher.group(3).split("\\s*,\\s*");
                        predicates.add(root.get(matcher.group(1)).in(list));
                    }
                }

            }
        }

        if (predicates.size() > 0)
            cq.where(predicates.toArray(new Predicate[0]));
        return this;
    }

    public ApiQuery<T> paginate() {

        this.query = em.createQuery(cq).setFirstResult(pagination.getSkip()).setMaxResults((pagination.getSkip() + 1) * pagination.getLimit());
        return this;
    }

    public ApiQuery<T> orderBy() {
        if (req.getParameter("orderBy") != null) {
            List<Order> orders = Arrays.stream(req.getParameter("orderBy").split("\\s*,\\s*")).toList().stream().map(x -> !x.contains("-") ? cb.asc(root.get(x)) : cb.desc(root.get(x.substring(1)))).toList();
            cq.orderBy(orders);
        }
        return this;
    }

    public List<T> exec() {

        if (this.query == null)
            return em.createQuery(cq).getResultList();
        else
            return query.getResultList();
    }
}

