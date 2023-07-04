package com.github.wireless4024.kfuzzy.field


import com.github.wireless4024.kfuzzy.faker.IFaker

object FieldString : FieldKind {
    override fun randomValue(faker: IFaker): Any = faker.fakeString()
}
