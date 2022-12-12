init:
	git submodule update --init
	cd rocket-chip && git submodule update --init hardfloat api-config-chipsalliance

compile:
	mill -i CoupledL2.compile

test-top-l2:
	mill -i CoupledL2.test.runMain coupledL2.TestTop_L2 -td build
	mv build/TestTop_L2.v build/TestTop.v

test-top-l2l3:
	mill -i CoupledL2.test.runMain coupledL2.TestTop_L2L3 -td build
	mv build/TestTop_L2L3.v build/TestTop.v

clean:
	rm -rf ./build

bsp:
	mill -i mill.bsp.BSP/install

idea:
	mill -i mill.scalalib.GenIdea/idea

reformat:
	mill -i __.reformat

checkformat:
	mill -i __.checkFormat
