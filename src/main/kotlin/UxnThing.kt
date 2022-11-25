import java.io.File

fun main() {
    val program = File("simpleadd.tal.rom").readBytes().toUByteArray()
    println(program.joinToString { it.toString(16) })
    val machine = UxnMachine()
    machine.loadRom(program)
    machine.debugPrint()
}

class UxnMachine {
    private val memory: MutableList<UByte> = MutableList(0xFFFF + 1) { 0u }
    fun debugPrint() {
        // 0x100: 80
        // 0x101: 0
        // ...
        // 0x205: 92
        memory.forEachIndexed { index, uByte ->
            println("0x${index.toString(16)}: ${uByte.toString(16)}")
        }
    }
    fun loadRom(rom: UByteArray) {
        // load rom at 0x100
        rom.forEachIndexed { idx, byte ->
            memory[0x100 + idx] = byte
        }
    }
}