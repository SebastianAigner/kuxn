import java.io.File
import Instruction.*

fun main() {
    val program = File("simpleadd.tal.rom").readBytes().toUByteArray()
    println(program.joinToString { it.toString(16) })
    val machine = UxnMachine()
    machine.loadRom(program)
    machine.debugPrint()
    var machineState: MachineState = MachineState.RUNNING
    while(machineState != MachineState.STOPPED) {
        machineState = machine.step()
    }
}

enum class MachineState {
    RUNNING,
    STOPPED
}

class UxnMachine {
    private val memory: MutableList<UByte> = MutableList(65536) { 0u }
    private val workingStack: ArrayDeque<UByte> = ArrayDeque(256)
    private var ip: UShort = 0x100u
    fun debugPrint() {
        // 0x100: 80
        // 0x101: 0
        // ...
        // 0x205: 92
        var zeroes = 0
        var shouldPrintEllipsis = true
        for(index in memory.indices) {
            val address = "0x${index.toString(16)}"
            val byte = memory[index]
            val value = byte.toString(16)
            if(byte == 0u.toUByte()) {
                zeroes++
            } else {
                zeroes = 0
                shouldPrintEllipsis = true
            }
            if(zeroes > 5) {
                if(shouldPrintEllipsis) {
                    println("...")
                    shouldPrintEllipsis = false
                }
                continue
            }
            println("$address: $value")
        }
    }

    fun step(): MachineState {
        if(ip.toInt() > memory.lastIndex) {
            println("Reached end of memory, terminating.")
            return MachineState.STOPPED
        }
        val instruction = memory[ip].toInstruction()
        ip++
        when(instruction) {
            LIT -> {
                workingStack.addLast(memory[ip])
                ip++
            }
            ADD -> {
                val a = workingStack.removeLast()
                val b = workingStack.removeLast()
                workingStack.addLast((a + b).toUByte())
            }
            DEO -> {
                val device = workingStack.removeLast()
                require(device == 0x18u.b) { "Currently only device known is console output (0x18)" }
                print(workingStack.removeLast().toInt().toChar())
            }
            BRK -> {
                return MachineState.STOPPED
            }
        }
        return MachineState.RUNNING
    }

    fun loadRom(rom: UByteArray) {
        // load rom at 0x100
        rom.forEachIndexed { idx, byte ->
            memory[0x100 + idx] = byte
        }
    }
}

enum class Instruction(val byte: UByte) {
    LIT(0x80u.b),
    ADD(0x18u.b),
    DEO(0x17u.b),
    BRK(0x0u.b)
}

val UInt.b: UByte get() {
    return this.toUByte()
}

fun UByte.toInstruction(): Instruction {
    return Instruction.values().find { it.byte == this } ?: error("Unknown instruction ${this.toString(16)}")
}

private operator fun <T> List<T>.get(uShort: UShort): T {
    return get(uShort.toInt())
}
