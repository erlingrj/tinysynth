package tinysynth
import chisel3._
import chisel3.util._

class PulseWidthModulator(cfg: TinySynthConfig) extends Module {
  val io = IO(new Bundle {
    val out = Output(Bool())
    val dutyCycle = Input(UInt(cfg.pwmOutputBits.W))
  })
  val counter = RegInit(0.U(cfg.pwmOutputBits.W))
  counter := counter + 1.U
  when(counter < io.dutyCycle) {
    io.out := true.B
  }.otherwise {
    io.out := false.B
  }
}
