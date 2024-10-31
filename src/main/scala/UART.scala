package tinysynth

import chisel3._
import chisel3.util._
import chisel.lib.uart.Rx

class UART(cfg: TinySynthConfig) extends Module {
  val io = IO(new Bundle {
    val in = Input(Bool())
    val out = Decoupled(UInt(8.W))
  })

  val rx = Module(new Rx(cfg.clockFrequency, cfg.uartBaudRate))
  rx.io.rxd := io.in.asTypeOf(rx.io.rxd)

  io.out <> rx.io.channel
}
