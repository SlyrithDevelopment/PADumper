# PAD (Process Android Dumper)
This dumper is made for il2cpp game but you can use it in any app you want

## How To Use
- Run the process
- Open PADumper
- Put process name manually or you can click `Select Apps` to select running apps
- Put the ELF Name or you can leave it with default name `libil2cpp.so`
- Check `global-metadata.dat` if you want dump unity metadata from memory
- Dump and wait process to finish
- Result will be in `/sdcard/PADumper/[Process]/[startAddress-nameFile]`

## Credits
- [libsu](https://github.com/topjohnwu/libsu)
