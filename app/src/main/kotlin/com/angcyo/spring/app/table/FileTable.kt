package com.angcyo.spring.app.table

import com.angcyo.spring.mybatis.plus.table.BaseAuditTable
import com.baomidou.mybatisplus.annotation.TableName
import com.gitee.sunchenbin.mybatis.actable.annotation.Column
import com.gitee.sunchenbin.mybatis.actable.annotation.TableComment
import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty

/**
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2021/06/07
 */

@TableName("file")
@TableComment("文件记录表, 记录上传的文件信息")
@ApiModel("文件数据结构")
class FileTable : BaseAuditTable() {

    @ApiModelProperty("文件的名字含后缀")
    @Column(comment = "文件的名字含后缀")
    var fileName: String? = null

    @ApiModelProperty("文件的访问地址含host")
    @Column(comment = "文件的访问地址含host")
    var fileUri: String? = null

    @ApiModelProperty("文件的相对路径")
    @Column(comment = "文件的相对路径")
    var filePath: String? = null

    @ApiModelProperty("文件的类型")
    @Column(comment = "文件的类型")
    var fileType: String? = null

    @ApiModelProperty("文件的大小(b)")
    @Column(comment = "文件的大小(b)")
    var fileSize: Long = 0

    @ApiModelProperty("文件的md5")
    @Column(comment = "文件的md5")
    var fileMd5: String? = null
}