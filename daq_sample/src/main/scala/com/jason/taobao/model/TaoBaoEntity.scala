package com.jason.taobao.model

import com.jason.model.Entity

case class TaoBaoEntity(id: Long,
                        name: String,
                        age: Int,
                        sex: Sex.Value,
                        marriage: Boolean,
                        address: String,
                        country: String) extends Entity
