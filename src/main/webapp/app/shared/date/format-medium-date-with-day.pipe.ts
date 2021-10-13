import { Pipe, PipeTransform } from '@angular/core';

import * as dayjs from 'dayjs';

// added by RJB

@Pipe({
  name: 'formatMediumDateWithDay',
})
export class FormatMediumDateWithDayPipe implements PipeTransform {
  transform(day: dayjs.Dayjs | null | undefined): string {
    return day ? day.format('ddd, DD MMM YYYY') : '';
  }
}
