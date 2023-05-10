package com.qzhou.constants;

public class SystemConstants {
    /**
     * 文章是草稿
     */
    public static final int ARTICLE_STATUS_DRAFT = 1;
    /**
     * 文章是正常分布状态
     */
    public static final int ARTICLE_STATUS_NORMAL = 0;
    /**
     * 文章的根评论 id
     */
    public static final int COMMENT_ROOT_ID = -1;

    /**
     * 菜单表中是第一层 / 父层
     */
    public static final int STATUS_PARENT_ID = 0;

    /**
     * 状态存在
     */
    public static final String STATUS_NORMAL = "0";

    /**
     * 状态不存在
     */
    public static final String STATUS_ABNORMAL = "1";

    /**
     * 友联状态为审核通过
     */
    public static final String LINK_STATUS_NORMAL = "0";
    /**
     * 评论类型为：文章评论
     */
    public static final String ARTICLE_COMMENT = "0";
    /**
     * 评论类型为：友联评论
     */
    public static final String LINK_COMMENT = "1";

    /**
     * 菜单
     */
    public static final String MENU = "C";
    /**
     * 目录
     */
    public static final String BUTTON = "F";
    /**
     * 后台用户
     */
    public static final String Admin_User = "1";

    /**
     * 文章置顶
     */
    public static final String ARTICLE_IS_TOP = "1";
    /**
     * 文章不置顶
     */
    public static final String ARTICLE_NOT_TOP = "0";
}