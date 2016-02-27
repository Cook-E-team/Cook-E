/*
 * Copyright 2016 the Cook-E development team
 *
 * This file is part of Cook-E.
 *
 * Cook-E is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Cook-E is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Cook-E.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.cook_e.data;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.CRC32;

/**
 * An OutputStream that calculates the CRC32 checksum of the bytes written to it
 */
class CrcOutputStream extends FilterOutputStream {

    /**
     * The CRC calculator
     */
    private CRC32 mCrc;

    /**
     * Creates a CRCOutputStream that wraps another stream
     * @param inner the stream to wrap. Must not be null.
     */
    public CrcOutputStream(OutputStream inner) {
        super(inner);
        mCrc = new CRC32();
    }

    /**
     * Writes a byte to the underlying stream and updates the CRC32 checksum to include
     * the written byte.
     *
     * If the underlying stream throws an exception, the checksum is not updated.
     *
     * @param oneByte the byte to write
     * @throws IOException if an error occurred writing the byte
     */
    @Override
    public void write(int oneByte) throws IOException {
        super.write(oneByte);
        mCrc.update(oneByte);
    }

    /**
     * Returns the CRC32 checksum of the bytes written so far
     * @return the checksum
     */
    public long getCrc() {
        return mCrc.getValue();
    }
}
