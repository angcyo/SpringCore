package com.angcyo.spring.mybatis.plus.auto.annotation

/**
 * where 条件
 * https://mp.baomidou.com/guide/wrapper.html#abstractwrapper
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2021/05/28
 */
enum class WhereEnum {

    /**
     * 等于 =
     * 例: eq("name", "老王")--->name = '老王'
     *
     * https://mp.baomidou.com/guide/wrapper.html#eq
     * */
    eq,

    /**
     * 不等于 <>
     * 例: ne("name", "老王")--->name <> '老王'
     * */
    ne,

    /**
     * 大于 >
     * 例: gt("age", 18)--->age > 18
     */
    gt,

    /**
     * 大于等于 >=
     * 例: ge("age", 18)--->age >= 18
     * */
    ge,

    /**
     * 小于 <
    例: lt("age", 18)--->age < 18
     */
    lt,

    /**
     * 小于等于 <=
    例: le("age", 18)--->age <= 18
     * */
    le,

    /**
     * BETWEEN 值1 AND 值2
    例: between("age", 18, 30)--->age between 18 and 30
     * */
    between,

    /**
     * NOT BETWEEN 值1 AND 值2
    例: notBetween("age", 18, 30)--->age not between 18 and 30
     * */
    notBetween,

    /**
     * LIKE '%值%'
    例: like("name", "王")--->name like '%王%'
     * */
    like,

    /**
     * NOT LIKE '%值%'
    例: notLike("name", "王")--->name not like '%王%'
     * */
    notLike,

    /**
     * LIKE '%值'
    例: likeLeft("name", "王")--->name like '%王'
     * */
    likeLeft,

    /**
     * LIKE '值%'
    例: likeRight("name", "王")--->name like '王%'
     * */
    likeRight,

    /**
     * 字段 IS NULL
    例: isNull("name")--->name is null
     * */
    isNull,

    /**
     * 字段 IS NOT NULL
    例: isNotNull("name")--->name is not null
     * */
    isNotNull,

    /**
     * 字段 IN (value.get(0), value.get(1), ...)
    例: in("age",{1,2,3})--->age in (1,2,3)
     * */
    `in`,

    /**
     * 字段 NOT IN (value.get(0), value.get(1), ...)
    例: notIn("age",{1,2,3})--->age not in (1,2,3)
     * */
    notIn,

    /**
     * 字段 IN ( sql语句 )
    例: inSql("age", "1,2,3,4,5,6")--->age in (1,2,3,4,5,6)
    例: inSql("id", "select id from table where id < 3")--->id in (select id from table where id < 3)
     * */
    inSql,

    /**
     * 字段 NOT IN ( sql语句 )
    例: notInSql("age", "1,2,3,4,5,6")--->age not in (1,2,3,4,5,6)
    例: notInSql("id", "select id from table where id < 3")--->id not in (select id from table where id < 3)
     * */
    notInSql,

    /**
     * 分组：GROUP BY 字段, ...
    例: groupBy("id", "name")--->group by id,name
     * */
    groupBy,

    /**
     * 排序：ORDER BY 字段, ... ASC
    例: orderByAsc("id", "name")--->order by id ASC,name ASC
     * */
    orderByAsc,

    /**
     * 排序：ORDER BY 字段, ... DESC
    例: orderByDesc("id", "name")--->order by id DESC,name DESC
     * */
    orderByDesc,

    /**
     * 排序：ORDER BY 字段, ...
    例: orderBy(true, true, "id", "name")--->order by id ASC,name ASC
     * */
    orderBy,

    /**
     * HAVING ( sql语句 )
    例: having("sum(age) > 10")--->having sum(age) > 10
    例: having("sum(age) > {0}", 11)--->having sum(age) > 11
     * */
    having,

    /**
     * func 方法(主要方便在出现if...else下调用不同方法能不断链)
    例: func(i -> if(true) {i.eq("id", 1)} else {i.ne("id", 1)})
     * */
    func,

    /**
     * 例: eq("id",1).or().eq("name","老王")--->id = 1 or name = '老王'
     * */
    or,

    /**
     * AND 嵌套
    例: and(i -> i.eq("name", "李白").ne("status", "活着"))--->and (name = '李白' and status <> '活着')
     * */
    and,

    /**
     * 例: apply("id = 1")--->id = 1
    例: apply("date_format(dateColumn,'%Y-%m-%d') = '2008-08-08'")--->date_format(dateColumn,'%Y-%m-%d') = '2008-08-08'")
    例: apply("date_format(dateColumn,'%Y-%m-%d') = {0}", "2008-08-08")--->date_format(dateColumn,'%Y-%m-%d') = '2008-08-08'")
     * */
    apply,

    /**
     * 无视优化规则直接拼接到 sql 的最后
     * 例: last("limit 1")
     * */
    last,

    /**
     * 拼接 EXISTS ( sql语句 )
    例: exists("select id from table where age = 1")--->exists (select id from table where age = 1)
     * */
    exists,

    /**
     * 拼接 NOT EXISTS ( sql语句 )
    例: notExists("select id from table where age = 1")--->not exists (select id from table where age = 1)
     * */
    notExists,

    /**忽略, 占位属性*/
    ignore,
}

