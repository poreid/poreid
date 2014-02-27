CC=cc
CFLAGS=-I/System/Library/Frameworks/JavaVM.framework/Versions/Current/Headers/ -I/System/Library/Frameworks/PCSC.framework/Headers/
LDFLAGS=-dynamiclib -framework PCSC -framework JavaVM
DEPS = org_poreid_macosx_smartcardio_PCSC.h org_poreid_macosx_smartcardio_PlatformPCSC.h
OBJ = pcsc.o pcsc_md.o

%.o: %.c $(DEPS)
	$(CC) -c -o $@ $< $(CFLAGS)

libj2pcsc.dylib: $(OBJ)
	$(CC) -o $@ $^ $(LDFLAGS)

.PHONY: clean

clean:
	rm -f *.o *.dylib