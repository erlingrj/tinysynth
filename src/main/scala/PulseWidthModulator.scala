package tinysynth
import chisel3._
import chisel3.util._

class PulseWidthModulator(cfg: TinySynthConfig) extends Module {
  def width = log2Ceil(cfg.pwmCyclesPerPeriod)
  val io = IO(new Bundle {
    val out = Output(Bool())
    val dutyCycle = Input(UInt(width.W))
  })
  val counter = RegInit(0.U(width.W))
  counter := counter + 1.U
  when(counter < io.dutyCycle) {
    io.out := true.B
  }.otherwise {
    io.out := false.B
  }
}
