package net.andreinc.serverneat.mockneat

import net.andreinc.mockneat.abstraction.MockUnit
import net.andreinc.mockneat.unit.objects.Constant.constant
import java.util.HashMap
import java.util.LinkedHashMap
import java.util.function.Supplier

class ObjectMap : MockUnit<Any> {

    private val map = LinkedHashMap<String, MockUnit<*>>()

    override fun supplier(): Supplier<Any> {
        return Supplier { traverseObject(this) }
    }

    infix fun String.value(unit: MockUnit<*>): ObjectMap {
        map[this@value] = unit
        return this@ObjectMap
    }

    infix fun String.const(value: Any) : ObjectMap {
        map[this@const] = constant(value)
        return this@ObjectMap
    }

    companion object {
        private fun traverseObject(ojMap: ObjectMap): Map<String, Any> {
            val map = ojMap.map
            val result = HashMap<String, Any>()
            for (key in map.keys) {
                val value = map[key]
                if (value is ObjectMap) {
                    result[key] = traverseObject(value)
                } else {
                    result[key] = value!!.get()
                }
            }
            return result
        }
    }
}
