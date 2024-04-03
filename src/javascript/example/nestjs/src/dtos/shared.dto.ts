import { IsString } from "class-validator";

export class SignatureDto {
    @IsString()
    r: string;
    @IsString()
    s: string;
  }
  