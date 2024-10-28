import chisel3._
import chiseltest._
import org.scalatest.flatspec.AnyFlatSpec
import tinysynth.DefaultConfig
import tinysynth.PulseWidthModulator

class PulseWidthModulatorTest extends AnyFlatSpec with ChiselScalatestTester {
  object cfg extends tinysynth.TinySynthConfig {
    override def pwmCyclesPerPeriod = 128
  }

  "ChiselTop" should "pass" in {
    test(new PulseWidthModulator(cfg)) { dut =>
      val dutyCycle = 15
      dut.io.dutyCycle.poke(dutyCycle)

      for (i <- 0 until dutyCycle) {
        dut.io.out.expect(true.B)
        dut.clock.step()
      }

      for (i <- 0 until (cfg.pwmCyclesPerPeriod - dutyCycle)) {
        dut.io.out.expect(false.B)
        dut.clock.step()
      }
    }
  }
}
