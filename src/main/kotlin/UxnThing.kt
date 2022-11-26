import java.io.File
import Instruction.*
import Screen.ShortScreenPort.*
import Screen.ByteScreenPort.*

fun main() {
    val program = File("screen.tal.rom").readBytes().toUByteArray()
    println(program.joinToString { it.toString(16) })
    val machine = UxnMachine()
    machine.loadRom(program)
    machine.debugPrint()
    var machineState: MachineState = MachineState.RUNNING
    while (machineState != MachineState.STOPPED) {
        println("==================")
        machineState = machine.step()
        machine.debugPrint()
    }
}

enum class MachineState {
    RUNNING,
    STOPPED
}

fun Pair<UByte, UByte>.toUShort(): UShort {
    val hi = this.first
    val lo = this.second
    return ((hi.toUInt() shl 8) + lo.toUInt()).toUShort()
}

fun UShort.toUBytes(): Pair<UByte, UByte> {
    val hi = (this.toUInt() shr 8).toUByte()
    val lo = (this.toUInt() and 0x00FFu).toUByte()
    return hi to lo
}

class MemoryLocation(val offset: Int, val length: Int)

class Screen(val memory: MutableList<UByte>) {
    interface ScreenPortable
    enum class ShortScreenPort : ScreenPortable {
        VECTOR,
        WIDTH,
        HEIGHT,
        X,
        Y,
        MEMORY_ADDRESS,
    }

    enum class ByteScreenPort : ScreenPortable {
        AUTO,
        PIXEL,
        SPRITE,
    }

    val offset = 20
    val locations = mapOf<ScreenPortable, MemoryLocation>(
        VECTOR to MemoryLocation(offset + 0, 2),
        WIDTH to MemoryLocation(offset + 2, 2),
        HEIGHT to MemoryLocation(offset + 4, 2),
        AUTO to MemoryLocation(offset + 6, 1),
        X to MemoryLocation(offset + 7, 2),
        Y to MemoryLocation(offset + 9, 2),
        MEMORY_ADDRESS to MemoryLocation(offset + 11, 2),
        PIXEL to MemoryLocation(offset + 13, 1),
        SPRITE to MemoryLocation(offset + 14, 1)
    )

    fun getBytePort(port: ByteScreenPort): UByte {
        val loc = locations[port]!! // todo: terrible!
        return memory[loc.offset]
    }

    fun getShortPort(port: ShortScreenPort): UShort {
        val loc = locations[port]!!  // todo: terrible!
        val hi = memory[loc.offset]
        val lo = memory[loc.offset + 1]
        return (hi to lo).toUShort()
    }
}

class UxnMachine {
    private val memory: MutableList<UByte> = MutableList(65536) { 0u }
    private val stack: ArrayDeque<UByte> = ArrayDeque(256)
    private var ip: UShort = 0x100u
    fun debugPrint() {
        // 0x100: 80
        // 0x101: 0
        // ...
        // 0x205: 92
        var zeroes = 0
        var shouldPrintEllipsis = true
        for (index in memory.indices) {
            val address = "0x${index.toString(16).padStart(4, '0')}"
            val byte = memory[index]
            val value = byte.toString(16).padStart(2, '0')
            if (byte == 0u.toUByte()) {
                zeroes++
            } else {
                zeroes = 0
                shouldPrintEllipsis = true
            }
            if (zeroes > 5) {
                if (shouldPrintEllipsis) {
                    println("...")
                    shouldPrintEllipsis = false
                }
                continue
            }
            println("$address: $value")
        }
    }

    fun step(): MachineState {
        if (ip.toInt() > memory.lastIndex) {
            println("Reached end of memory, terminating.")
            return MachineState.STOPPED
        }
        val instruction = memory[ip].toInstruction()
        ip++
        when (instruction) {
            LIT -> {
                stack.addLast(memory[ip])
                ip++
            }

            ADD -> {
                val a = stack.removeLast()
                val b = stack.removeLast()
                stack.addLast((a + b).toUByte())
            }

            DEO -> {
                val device = stack.removeLast()
                require(device == 0x18u.b) { "Currently only device known is console output (0x18)" }
                print(stack.removeLast().toInt().toChar())
            }

            BRK -> {
                return MachineState.STOPPED
            }

            INC -> {
                val a = stack.removeLast()
                stack.addLast((a + 1u).toUByte())
            }

            DEO2 -> {
                val device = stack.removeLast()
                val lo = stack.removeLast()
                val hi = stack.removeLast()
                memory[device.toInt()] = hi
                memory[device.toInt() + 1] = lo
                // TODO: The target device might capture the writing to trigger an I/O event.
            }

            ADD2 -> {
                val lo = stack.removeLast()
                val hi = stack.removeLast()
                val short1 = (hi to lo).toUShort()
                val lo2 = stack.removeLast()
                val hi2 = stack.removeLast()
                val short2 = (hi2 to lo2).toUShort()
                val res = (short1 + short2).toUShort()
                val (hiRes, loRes) = res.toUBytes()
                stack.addLast(hiRes)
                stack.addLast(loRes)
            }

            LIT2 -> {
                val hi = memory[ip]
                ip++
                val lo = memory[ip]
                ip++
                stack.addLast(hi)
                stack.addLast(lo)
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
    LIT2(0xa0u.b),
    ADD(0x18u.b),
    ADD2(0x38u.b),
    DEO(0x17u.b),
    BRK(0x0u.b),
    INC(0x01u.b),
    DEO2(0x37u.b),
}

val UInt.b: UByte
    get() {
        return this.toUByte()
    }

fun UByte.toInstruction(): Instruction {
    return Instruction.values().find { it.byte == this } ?: error("Unknown instruction ${this.toString(16)}")
}

private operator fun <T> List<T>.get(uShort: UShort): T {
    return get(uShort.toInt())
}
