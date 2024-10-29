package tinysynth

import chisel3._
import chisel3.util._


class InputADC(cfg: TinySynthConfig) extends Module {
  val io = IO(new Bundle {
    val analogIn = Input(Bool())
    val analogOut = Output(Bool())
    val digitalOut = Output(UInt(cfg.uiKnobInputBits.W))
  })


  val cnt = RegInit(0.U(log2Ceil(cfg.pwmCyclesPerPeriod).W))
  val numHighs = RegInit(0.U(log2Ceil(cfg.pwmCyclesPerPeriod).W))
  val regDigitalOut = RegInit(0.U(cfg.uiKnobInputBits.W))

  val regs = RegInit(VecInit(Seq.fill(cfg.adcNumPipelineRegs)(false.B)))
  regs(0) := io.analogIn
  for (i <- 1 until cfg.adcNumPipelineRegs) {
    regs(i) := regs(i-1)
  }
  io.analogOut := regs.last

  when(regs.last) {
    numHighs := numHighs + 1.U
  }

  io.digitalOut := regDigitalOut

  cnt := cnt + 1.U
  // FIXME: Select a value that overflows nicely
  when (cnt === (cfg.pwmCyclesPerPeriod-1).U) {
    regDigitalOut := numHighs
    cnt := 0.U
    numHighs := 0.U
  }
}
